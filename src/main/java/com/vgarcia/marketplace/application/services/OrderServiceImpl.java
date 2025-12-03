package com.vgarcia.marketplace.application.services;

import com.opencsv.bean.CsvToBeanBuilder;
import com.vgarcia.marketplace.application.dto.ImportResultDTO;
import com.vgarcia.marketplace.application.dto.OrderDTO;
import com.vgarcia.marketplace.application.dto.OrderItemDTO;
import com.vgarcia.marketplace.application.dto.csv.OrderImportCsvDTO;
import com.vgarcia.marketplace.application.dto.request.UpdateOrderRequest;
import com.vgarcia.marketplace.application.helpers.GenericExportHelper;
import com.vgarcia.marketplace.application.usecase.InventoryService;
import com.vgarcia.marketplace.application.usecase.OrderService;
import com.vgarcia.marketplace.domain.exception.ClientNotFoundException;
import com.vgarcia.marketplace.domain.exception.InsufficientStockException;
import com.vgarcia.marketplace.domain.exception.OrderNotFoundException;
import com.vgarcia.marketplace.domain.exception.ProductNotFoundException;
import com.vgarcia.marketplace.domain.models.*;
import com.vgarcia.marketplace.domain.ports.ClientPersistencePort;
import com.vgarcia.marketplace.domain.ports.OrderPersistencePort;
import com.vgarcia.marketplace.domain.ports.ProductPersistencePort;
import com.vgarcia.marketplace.infraestructure.enums.OrderStatus;
import com.vgarcia.marketplace.infraestructure.mappers.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.vgarcia.marketplace.infraestructure.utils.Constans.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderPersistencePort orderPersistencePort;
    private final ProductPersistencePort productPersistencePort;
    private final InventoryService inventoryService;
    private final ClientPersistencePort clientPersistencePort;
    private final OrderMapper orderMapper;
    private final GenericExportHelper genericExportHelper;


    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    @Transactional
    public OrderDTO create(OrderDTO orderToCreate) {
        logger.info("Iniciando creación de orden para el cliente ID: {}", orderToCreate.clientId());

        clientPersistencePort.findById(orderToCreate.clientId())
                .orElseThrow(() -> new ClientNotFoundException(orderToCreate.clientId()));

        Map<Long, ProductDomain> productMap = validateStockAndGetProducts(orderToCreate.items());

        List<OrderItemDomain> itemDomains = new ArrayList<>();
        for (OrderItemDTO itemDto : orderToCreate.items()) {
            ProductDomain product = productMap.get(itemDto.productId());
            CommercialDomain commercial = product.commercial();

            inventoryService.reserveStock(product.id(), itemDto.quantity());

            itemDomains.add(new OrderItemDomain(
                    product.id(),
                    itemDto.quantity(),
                    commercial.calculateSellingPrice()
            ));
        }

        BigDecimal taxRate = productMap.values().stream()
                .findFirst()
                .map(p -> p.commercial().taxRate())
                .orElse(BigDecimal.ZERO);

        OrderDomain domainToSave = new OrderDomain(orderToCreate.clientId(), itemDomains, taxRate);

        OrderDomain savedOrder = orderPersistencePort.save(domainToSave);
        logger.info("Orden ID: {} creada exitosamente para el cliente ID: {}", savedOrder.id(), savedOrder.clientId());

        clientPersistencePort.updateLastActivityDate(orderToCreate.clientId());

        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDTO update(Long id, UpdateOrderRequest orderToUpdate) {
        logger.info("Iniciando actualización de la orden ID: {}", id);

        OrderDomain existingOrder = orderPersistencePort.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (existingOrder.status() != OrderStatus.PENDING) {
            throw new IllegalStateException("Solo se pueden modificar órdenes en estado PENDIENTE. Estado actual: " + existingOrder.status());
        }

        existingOrder.items().forEach(item -> inventoryService.releaseStock(item.productId(), item.quantity()));


        Map<Long, ProductDomain> productMap = validateStockAndGetProducts(orderToUpdate.items());

        List<OrderItemDomain> newItemDomains = new ArrayList<>();
        for (OrderItemDTO itemDto : orderToUpdate.items()) {
            ProductDomain product = productMap.get(itemDto.productId());
            inventoryService.reserveStock(product.id(), itemDto.quantity());
            newItemDomains.add(new OrderItemDomain(
                    product.id(),
                    itemDto.quantity(),
                    product.commercial().calculateSellingPrice()
            ));
        }

        BigDecimal taxRate = productMap.values().stream().findFirst()
                .map(p -> p.commercial().taxRate()).orElse(BigDecimal.ZERO);

        OrderDomain domainToUpdate = new OrderDomain(
                existingOrder.id(),
                existingOrder.clientId(),
                newItemDomains,
                taxRate,
                existingOrder.status(),
                existingOrder.createdAt()
        );

        OrderDomain updatedDomain = orderPersistencePort.save(domainToUpdate);
        logger.info("Orden ID: {} actualizada exitosamente.", updatedDomain.id());

        return orderMapper.toDto(updatedDomain);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        orderPersistencePort.findById(id).ifPresent(order -> {
            if (order.status() == OrderStatus.PENDING || order.status() == OrderStatus.PROCESSING) {
                order.items().forEach(item -> inventoryService.releaseStock(item.productId(), item.quantity()));
            }
            orderPersistencePort.deleteById(id);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
        return orderPersistencePort.findById(id)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> findAll() {
        return orderMapper.toDtoList(orderPersistencePort.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> findOrdersByClientId(Long clientId) {
        return orderMapper.toDtoList(orderPersistencePort.findByClientId(clientId));
    }


    private Map<Long, ProductDomain> validateStockAndGetProducts(List<OrderItemDTO> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La orden debe contener al menos un item.");
        }
        Set<Long> productIds = items.stream().map(OrderItemDTO::productId).collect(Collectors.toSet());
        Map<Long, ProductDomain> productMap = productPersistencePort.findByIds(productIds).stream()
                .collect(Collectors.toMap(ProductDomain::id, Function.identity()));

        for (OrderItemDTO itemDto : items) {
            ProductDomain product = productMap.get(itemDto.productId());
            if (product == null) {
                throw new ProductNotFoundException(itemDto.productId());
            }
            if (product.inventory().getAvailableStock() < itemDto.quantity()) {
                throw new InsufficientStockException(product.id(), itemDto.quantity(), product.inventory().getAvailableStock());
            }
        }
        return productMap;
    }

    @Override
    @Transactional
    public ImportResultDTO importFromCsv(InputStream inputStream) {
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            List<OrderImportCsvDTO> csvRows = new CsvToBeanBuilder<OrderImportCsvDTO>(reader)
                    .withType(OrderImportCsvDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            if (csvRows.isEmpty()) {
                return new ImportResultDTO(0, 0, 0, List.of());
            }

            Map<String, List<OrderImportCsvDTO>> ordersByGroupingId = csvRows.stream()
                    .collect(Collectors.groupingBy(OrderImportCsvDTO::getGroupingId));

            final List<String> errors = new ArrayList<>();
            final List<OrderDTO> createdOrders = new ArrayList<>();
            int totalProcessed = 0;

            for (Map.Entry<String, List<OrderImportCsvDTO>> entry : ordersByGroupingId.entrySet()) {
                String group = entry.getKey();
                List<OrderImportCsvDTO> orderItemsRows = entry.getValue();
                totalProcessed += orderItemsRows.size();

                try {
                    OrderDTO orderToCreate = getOrderDTO(orderItemsRows, group);
                    OrderDTO createdOrder = this.create(orderToCreate);
                    createdOrders.add(createdOrder);
                } catch (Exception e) {
                    errors.add(ERROR_GRUPO + group + "': " + e.getMessage());
                }
            }
            return new ImportResultDTO(totalProcessed, createdOrders.size(), errors.size(), errors);
        } catch (Exception e) {
            throw new RuntimeException(FALLO_AL_PROCESAR_EL_ARCHIVO_CSV + e.getMessage(), e);
        }
    }

    private OrderDTO getOrderDTO(List<OrderImportCsvDTO> orderItemsRows, String group) {
        Long clientId = orderItemsRows.get(0).getClientId();
        if (clientId == null) {
            throw new IllegalArgumentException(EL_CLIENT_ID_NO_NULO_EN_EL_GRUPO + group + "'.");
        }

        List<OrderItemDTO> itemsDto = new ArrayList<>();
        for (OrderImportCsvDTO row : orderItemsRows) {
            if (row.getProductId() == null) {
                throw new IllegalArgumentException(PRODUCT_ID_NO_PUEDE_SER_NULO + group + "'.");
            }
            if (row.getQuantity() == null || row.getQuantity() <= 0) {
                throw new IllegalArgumentException(LA_QUANTITY_DEBE_SER_NÚMERO_POSITIVO + group + "'.");
            }
            itemsDto.add(new OrderItemDTO(null, null, null, null, null, row.getProductId(), row.getQuantity()));
        }
        return new OrderDTO(null, null, null, null, null, null, null, null, clientId, itemsDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportOrdersToPdf() {
        List<OrderDTO> orders = this.findAll();
        List<String> headers = List.of("ID", "Client ID", "Subtotal", "Impuesto", "Total", "Estado", "Fec. Creación");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        List<Function<OrderDTO, String>> mappers = List.of(
                order -> Objects.toString(order.id(), ""),
                order -> Objects.toString(order.clientId(), ""),
                order -> order.subtotal().toPlainString(),
                order -> order.tax().toPlainString(),
                order -> order.total().toPlainString(),
                order -> order.status().name(),
                order -> order.orderDate() != null ? order.orderDate().format(formatter) : ""
        );
        return genericExportHelper.exportToPdf("Listado de Órdenes", headers, orders, mappers);
    }

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportOrdersToExcel() {
        List<OrderDTO> orders = this.findAll();
        List<String> headers = List.of("ID", "Client ID", "Subtotal", "Impuesto", "Total", "Estado", "Fecha Creación", "Fecha Actualización");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        List<Function<OrderDTO, String>> mappers = List.of(
                order -> Objects.toString(order.id(), ""),
                order -> Objects.toString(order.clientId(), ""),
                order -> order.subtotal().toPlainString(),
                order -> order.tax().toPlainString(),
                order -> order.total().toPlainString(),
                order -> order.status().name(),
                order -> order.orderDate() != null ? order.orderDate().format(formatter) : "",
                order -> order.updatedAt() != null ? order.updatedAt().format(formatter) : ""
        );
        return genericExportHelper.exportToExcel("Órdenes", headers, orders, mappers);
    }
}
