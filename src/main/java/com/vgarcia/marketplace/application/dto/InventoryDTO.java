package com.vgarcia.marketplace.application.dto;

import com.vgarcia.marketplace.infraestructure.enums.ProductStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryDTO(
        int currentStock,
        int reservedStock,
        int availableStock,
        String warehouseLocation,
        ProductStatus status
) {
    public record StockAdjustmentDTO(
            @NotNull(message = "La cantidad no puede ser nula.")
            @Min(value = 1, message = "La cantidad a ajustar debe ser al menos 1.")
            int quantity
    ) {}
}
