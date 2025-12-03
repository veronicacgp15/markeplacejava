package com.vgarcia.marketplace.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryRequestDTO (
        @NotNull(message = "El stock inicial es obligatorio.")
        @Min(value = 0, message = "El stock inicial no puede ser negativo.")
        Integer initialStock,

        String warehouseLocation
){
}
