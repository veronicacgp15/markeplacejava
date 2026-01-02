package com.vgarcia.marketplace.domain.models;

import com.vgarcia.marketplace.infrastructure.enums.ProductStatus;

import java.time.LocalDateTime;

import static com.vgarcia.marketplace.infrastructure.utils.Constans.*;


public record InventoryDomain(
        Long productId,
        int currentStock,
        int reservedStock,
        String warehouseLocation,
        ProductStatus status,
        Long version,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {


    public InventoryDomain(int initialStock, String warehouseLocation) {
        this(
                null,
                initialStock,
                0,
                warehouseLocation,
                initialStock > 0 ? ProductStatus.AVAILABLE : ProductStatus.OUT_OF_STOCK,
                null,
                null,
                null
        );
    }

    public int getAvailableStock() {
        return this.currentStock;
    }

    public InventoryDomain reserveStock(int quantity) {
        if (getAvailableStock() < quantity) {
            throw new IllegalStateException(STOCK_INSUFICIENTE_DISPONIBLE + getAvailableStock() + ", solicitado: " + quantity);
        }
        return new InventoryDomain(
                this.productId,
                this.currentStock - quantity,
                this.reservedStock + quantity,
                this.warehouseLocation,
                this.status,
                this.version,
                this.createdAt,
                this.updatedAt
        ).updateStatus();
    }

    public InventoryDomain releaseStock(int quantity) {
        int newReservedStock = this.reservedStock - quantity;
        int newCurrentStock = this.currentStock + quantity;

        if (newReservedStock < 0) {
            newCurrentStock = this.currentStock + this.reservedStock;
            newReservedStock = 0;
        }

        return new InventoryDomain(
                this.productId,
                newCurrentStock,
                newReservedStock,
                this.warehouseLocation,
                this.status,
                this.version,
                this.createdAt,
                this.updatedAt
        ).updateStatus();
    }

    public InventoryDomain commitSale(int quantity) {
        if (this.reservedStock < quantity) {
            throw new IllegalStateException(INCONSISTENCIA_SE_INTENTA_CONFIRMAR_UNA_VENTA_POR + quantity + " unidades, pero solo hay " + this.reservedStock + " reservadas.");
        }
        return new InventoryDomain(
                this.productId,
                this.currentStock,
                this.reservedStock - quantity,
                this.warehouseLocation,
                this.status,
                this.version,
                this.createdAt,
                this.updatedAt
        ).updateStatus();
    }

    public InventoryDomain addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(CANTIDAD_A_AÑADIR_DEBE_SER_POSITIVA);
        }
        return new InventoryDomain(
                this.productId,
                this.currentStock + quantity,
                this.reservedStock,
                this.warehouseLocation,
                this.status,
                this.version,
                this.createdAt,
                this.updatedAt
        ).updateStatus();
    }

    private InventoryDomain updateStatus() {
        ProductStatus newStatus;
        if (this.currentStock <= 0) {
            newStatus = ProductStatus.OUT_OF_STOCK;
        } else {
            newStatus = ProductStatus.AVAILABLE;
        }

        if (this.status == newStatus) {
            return this;
        }

        return new InventoryDomain(
                this.productId,
                this.currentStock,
                this.reservedStock,
                this.warehouseLocation,
                newStatus,
                this.version,
                this.createdAt,
                this.updatedAt
        );
    }
}
