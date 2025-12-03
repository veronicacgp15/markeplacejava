package com.vgarcia.marketplace.application.usecase;

public interface InventoryService {

    void reserveStock(Long productId, int quantity);

    void releaseStock(Long productId, int quantity);

    void commitSale(Long productId, int quantity);

    void addStock(Long productId, int quantity);
}
