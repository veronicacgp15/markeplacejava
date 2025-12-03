package com.vgarcia.marketplace.application.services;

import com.opencsv.bean.CsvToBeanBuilder;
import com.vgarcia.marketplace.application.dto.ImportResultDTO;
import com.vgarcia.marketplace.application.dto.ProductDTO;
import com.vgarcia.marketplace.application.dto.csv.ProductCsvDTO;
import com.vgarcia.marketplace.application.helpers.GenericExportHelper;
import com.vgarcia.marketplace.application.usecase.ProductService;
import com.vgarcia.marketplace.domain.exception.CategoryNotFoundException;
import com.vgarcia.marketplace.domain.exception.ProductNotFoundException;
import com.vgarcia.marketplace.domain.models.CategoryDomain;
import com.vgarcia.marketplace.domain.models.CommercialDomain;
import com.vgarcia.marketplace.domain.models.InventoryDomain;
import com.vgarcia.marketplace.domain.models.ProductDomain;
import com.vgarcia.marketplace.domain.ports.CategoryPersistencePort;
import com.vgarcia.marketplace.domain.ports.ProductPersistencePort;
import com.vgarcia.marketplace.infraestructure.mappers.ProductMapper;
import com.vgarcia.marketplace.infraestructure.utils.Constans;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static com.vgarcia.marketplace.infraestructure.utils.Constans.EXISTE_UN_PRODUCTO_CON_EL_SKU;
import static com.vgarcia.marketplace.infraestructure.utils.Constans.NO_SE_PUDO_PROCESAR_EL_ARCHIVO_CSV_CAUSA;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductPersistencePort productPersistencePort;
    private final CategoryPersistencePort categoryPersistencePort;
    private final ProductMapper productMapper;
    private final GenericExportHelper exportHelper;

    @Override
    @Transactional
    public ProductDTO create(ProductDTO productDTO) {
        log.info("Iniciando creación de producto con SKU: {}", productDTO.sku());

        if (productPersistencePort.existsBySku(productDTO.sku())) {
            throw new IllegalStateException(EXISTE_UN_PRODUCTO_CON_EL_SKU + productDTO.sku());
        }
        CategoryDomain category = categoryPersistencePort.findById(productDTO.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(productDTO.categoryId()));

        ProductDomain domainToSave = getProductDomain(productDTO, category);

        ProductDomain savedDomain = productPersistencePort.save(domainToSave);
        log.info("Producto con ID: {} creado exitosamente", savedDomain.id());
        return productMapper.toDto(savedDomain);
    }

    private static ProductDomain getProductDomain(ProductDTO productDTO, CategoryDomain category) {
        InventoryDomain inventoryDomain = new InventoryDomain(
                productDTO.inventoryRequest().initialStock(),
                productDTO.inventoryRequest().warehouseLocation()
        );

        CommercialDomain commercialDomain = new CommercialDomain(
                productDTO.commercialRequest().basePrice(),
                productDTO.commercialRequest().taxRate()
        );

        ProductDomain domainToSave = new ProductDomain(
                productDTO.sku(),
                productDTO.name(),
                productDTO.description(),
                category,
                commercialDomain,
                inventoryDomain,
                null
        );
        return domainToSave;
    }

    @Override
    @Transactional
    public ProductDTO update(Long id, ProductDTO productDTO) {
        log.info("Iniciando actualización del producto con ID: {}", id);
        ProductDomain existingDomain = productPersistencePort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        CategoryDomain categoryToUse = existingDomain.category();
        Long newCategoryId = productDTO.categoryId();
        if (newCategoryId != null && !newCategoryId.equals(existingDomain.category().id())) {
            log.info("Cambiando categoría del producto {} a la nueva categoría ID: {}",
                    id, newCategoryId);
            categoryToUse = categoryPersistencePort.findById(newCategoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(newCategoryId));
        }

        ProductDomain domainToUpdate = new ProductDomain(
                existingDomain.id(),
                existingDomain.sku(),
                Optional.ofNullable(productDTO.name()).orElse(existingDomain.name()),
                Optional.ofNullable(productDTO.description()).orElse(existingDomain.description()),
                categoryToUse,
                existingDomain.commercial(),
                existingDomain.inventory(),
                existingDomain.metadata()
        );

        ProductDomain updatedDomain = productPersistencePort.save(domainToUpdate);
        log.info("Producto con ID: {} actualizado exitosamente", updatedDomain.id());
        return productMapper.toDto(updatedDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        log.info("Buscando producto con ID: {}", id);
        return productPersistencePort.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        log.info("Buscando todos los productos de forma paginada: {}", pageable);
        Page<ProductDomain> domainPage = productPersistencePort.findAll(pageable);
        return domainPage.map(productMapper::toDto);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.warn("Iniciando eliminación del producto con ID: {}", id);
        if (!productPersistencePort.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productPersistencePort.deleteById(id);
        log.info("Producto con ID: {} eliminado", id);
    }

    @Override
    @Transactional
    public ImportResultDTO importFromCsv(InputStream inputStream) {
        log.info("Iniciando importación de productos desde CSV");
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            List<ProductCsvDTO> csvDtoList = new CsvToBeanBuilder<ProductCsvDTO>(reader)
                    .withType(ProductCsvDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            if (csvDtoList.isEmpty()) {
                return new ImportResultDTO(0, 0, 0, List.of());
            }

            Set<String> existingSkus = productPersistencePort.findAllSkus();
            Set<Long> existingCategoryIds = categoryPersistencePort.findAllIds();
            List<String> errors = new ArrayList<>();
            List<ProductDomain> productsToCreate = new ArrayList<>();

            for (int i = 0; i < csvDtoList.size(); i++) {
                ProductCsvDTO dto = csvDtoList.get(i);
                int rowNum = i + 2;

                if (dto.getSku() == null || dto.getSku().isBlank()) {
                    errors.add("Fila " + rowNum + ": El SKU es obligatorio.");
                    continue;
                }
                if (existingSkus.contains(dto.getSku())) {
                    errors.add("Fila " + rowNum + ": El SKU '" + dto.getSku() + "' ya existe.");
                    continue;
                }
                if (dto.getCategoryId() == null || !existingCategoryIds.contains(dto.getCategoryId())) {
                    errors.add("Fila " + rowNum + ": La categoría con ID '" + dto.getCategoryId() + "' no existe.");
                    continue;
                }

                ProductDomain newDomain = productMapper.fromCsvToDomain(dto);
                productsToCreate.add(newDomain);
                existingSkus.add(newDomain.sku());
            }

            if (!productsToCreate.isEmpty()) {
                productPersistencePort.saveAll(productsToCreate);
            }

            log.info("Importación de productos completada. Filas procesadas: {}, Creadas: {}, Errores: {}",
                    csvDtoList.size(), productsToCreate.size(), errors.size());
            return new ImportResultDTO(csvDtoList.size(), productsToCreate.size(), errors.size(), errors);

        } catch (Exception e) {
            log.error("Fallo crítico durante la importación del CSV de productos.", e);
            throw new RuntimeException(NO_SE_PUDO_PROCESAR_EL_ARCHIVO_CSV_CAUSA + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportToPdf() {
        List<ProductDTO> products = productPersistencePort.findAllWithDetails().stream()
                .map(productMapper::toDto).collect(Collectors.toList());
        List<String> headers = List.of("ID", "SKU", "Nombre", "Precio", "Stock Disp.");
        List<Function<ProductDTO, String>> mappers = List.of(
                product -> Objects.toString(product.id(), ""),
                ProductDTO::sku,
                ProductDTO::name,
                product -> product.commercial() != null ?
                        product.commercial().basePrice().toPlainString() : "N/A",
                product -> product.inventory() != null ?
                        String.valueOf(product.inventory().availableStock()) : "N/A"
        );
        return exportHelper.exportToPdf("Listado de Productos", headers, products, mappers);
    }

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportToExcel() {
        List<ProductDTO> products = productPersistencePort.findAllWithDetails().stream()
                .map(productMapper::toDto).collect(Collectors.toList());
        List<String> headers = List.of("ID", "SKU", "Nombre", "Descripción", "Precio Base", "Stock Actual", "Stock Reservado");
        List<Function<ProductDTO, String>> mappers = List.of(
                product -> Objects.toString(product.id(), ""),
                ProductDTO::sku,
                ProductDTO::name,
                ProductDTO::description,
                product -> product.commercial() != null ? product.commercial().basePrice().toPlainString() : "N/A",
                product -> product.inventory() != null ? String.valueOf(product.inventory().currentStock()) : "N/A",
                product -> product.inventory() != null ? String.valueOf(product.inventory().reservedStock()) : "N/A"
        );
        return exportHelper.exportToExcel("Productos", headers, products, mappers);
    }
}
