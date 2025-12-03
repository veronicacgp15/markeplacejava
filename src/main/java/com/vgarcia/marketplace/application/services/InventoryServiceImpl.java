package com.vgarcia.marketplace.application.services;

import com.vgarcia.marketplace.application.dto.ImportResultDTO;
import com.vgarcia.marketplace.application.dto.InventoryDTO;
import com.vgarcia.marketplace.application.dto.csv.InventoryCsvDTO;
import com.vgarcia.marketplace.application.helpers.GenericExportHelper;
import com.vgarcia.marketplace.application.helpers.GenericImportHelper;
import com.vgarcia.marketplace.application.usecase.InventoryService;
import com.vgarcia.marketplace.domain.exception.InventoryNotFoundException;
import com.vgarcia.marketplace.domain.exception.ProductNotFoundException;
import com.vgarcia.marketplace.domain.models.InventoryDomain;
import com.vgarcia.marketplace.domain.ports.InventoryPersistencePort;
import com.vgarcia.marketplace.infraestructure.mappers.InventoryMapper;
import lombok.RequiredArgsConstructor;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

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
