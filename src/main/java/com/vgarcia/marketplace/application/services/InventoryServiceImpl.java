package com.vgarcia.marketplace.application.services;

import com.vgarcia.marketplace.application.usecase.InventoryService;
import com.vgarcia.marketplace.domain.exception.ProductNotFoundException;
import com.vgarcia.marketplace.domain.models.InventoryDomain;
import com.vgarcia.marketplace.domain.ports.InventoryPersistencePort;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryPersistencePort inventoryPersistencePort;

    @Override
    @Transactional
    public void reserveStock(Long productId, int quantity) {

        InventoryDomain inventory = inventoryPersistencePort.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));


        InventoryDomain updatedInventory = inventory.reserveStock(quantity);

        inventoryPersistencePort.save(updatedInventory);
    }

    @Override
    @Transactional
    public void releaseStock(Long productId, int quantity) {
        InventoryDomain inventory = inventoryPersistencePort.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        InventoryDomain updatedInventory = inventory.releaseStock(quantity);

        inventoryPersistencePort.save(updatedInventory);
    }

    @Override
    @Transactional
    public void commitSale(Long productId, int quantity) {
        InventoryDomain inventory = inventoryPersistencePort.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        InventoryDomain updatedInventory = inventory.commitSale(quantity);

        inventoryPersistencePort.save(updatedInventory);
    }

    @Override
    @Transactional
    public void addStock(Long productId, int quantity) {
        InventoryDomain inventory = inventoryPersistencePort.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        InventoryDomain updatedInventory = inventory.addStock(quantity);

        inventoryPersistencePort.save(updatedInventory);
    }
}
