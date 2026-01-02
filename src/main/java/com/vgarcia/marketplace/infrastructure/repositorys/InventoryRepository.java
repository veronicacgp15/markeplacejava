package com.vgarcia.marketplace.infrastructure.repositorys;

import com.vgarcia.marketplace.infrastructure.entities.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    @Query("SELECT i FROM Inventory i WHERE (i.currentStock - i.reservedStock) > 0")
    List<Inventory> findAllInventoriesAvailable();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId")
    Optional<Inventory> findByProductIdWithLock(Long productId);

    @EntityGraph(attributePaths = {"product"})
    List<Inventory> findByCurrentStockLessThan(Integer currentStock);



}
