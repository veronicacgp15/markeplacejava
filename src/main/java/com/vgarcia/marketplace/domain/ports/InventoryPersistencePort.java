package com.vgarcia.marketplace.domain.ports;

import com.vgarcia.marketplace.domain.models.InventoryDomain;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface InventoryPersistencePort {

    InventoryDomain save(InventoryDomain inventoryDomain);

    List<InventoryDomain> saveAll(List<InventoryDomain> inventoryDomains);

    Optional<InventoryDomain> findByProductId(Long productId);

    List<InventoryDomain> findByIds(Set<Long> productIds);

    List<InventoryDomain> findAll();

}
