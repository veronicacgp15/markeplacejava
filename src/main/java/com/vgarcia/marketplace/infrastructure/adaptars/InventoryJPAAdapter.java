package com.vgarcia.marketplace.infrastructure.adaptars;

import com.vgarcia.marketplace.domain.models.InventoryDomain;
import com.vgarcia.marketplace.domain.ports.InventoryPersistencePort;
import com.vgarcia.marketplace.infrastructure.entities.Inventory;
import com.vgarcia.marketplace.infrastructure.mappers.InventoryMapper;
import com.vgarcia.marketplace.infrastructure.repositorys.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InventoryJPAAdapter implements InventoryPersistencePort {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<InventoryDomain> findByProductId(Long productId) {
        return inventoryRepository.findById(productId)
                .map(inventoryMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDomain> findByIds(Set<Long> productIds) {
        List<Inventory> entities = inventoryRepository.findAllById(productIds);
        return inventoryMapper.toDomainList(entities);
    }


    @Override
    @Transactional
    public InventoryDomain save(InventoryDomain inventoryDomain) {
        Inventory inventoryEntity = inventoryRepository.findById(inventoryDomain.productId())
                .orElseThrow(() -> new IllegalStateException("No se puede guardar un inventario para un producto inexistente: " + inventoryDomain.productId()));

        inventoryMapper.updateEntityFromDomain(inventoryDomain, inventoryEntity);

        Inventory savedEntity = inventoryRepository.save(inventoryEntity);
        return inventoryMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public List<InventoryDomain> saveAll(List<InventoryDomain> inventoryDomains) {
        Set<Long> ids = inventoryDomains.stream()
                .map(InventoryDomain::productId)
                .collect(Collectors.toSet());

        Map<Long, Inventory> entityMap = inventoryRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Inventory::getId, entity -> entity));

        for (InventoryDomain domain : inventoryDomains) {
            Inventory entityToUpdate = entityMap.get(domain.productId());
            if (entityToUpdate != null) {
                inventoryMapper.updateEntityFromDomain(domain, entityToUpdate);
            }
        }

        List<Inventory> savedEntities = inventoryRepository.saveAll(entityMap.values());
        return inventoryMapper.toDomainList(savedEntities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDomain> findAll() {
        return inventoryMapper.toDomainList(inventoryRepository.findAll());
    }


    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(readOnly = true)
    public void checkLowStockDailyReport() {
        List<Inventory> lowStockItems = inventoryRepository.findAll().stream()
                .filter(inv -> inv.getCurrentStock() < 5)
                .toList();

        if (lowStockItems.isEmpty()) {
            System.out.println("--- REPORTE DIARIO: No hay productos con bajo stock hoy ---");
            return;
        }

        System.out.println("--- REPORTE DIARIO DE BAJO STOCK (" + new Date() + ") ---");
        lowStockItems.forEach(item ->
                System.out.println("ALERTA: Producto ID: " + item.getProduct().getId() +
                        " | SKU: " + item.getProduct().getSku() +
                        " | Stock Actual: " + item.getCurrentStock())
        );
    }
}
