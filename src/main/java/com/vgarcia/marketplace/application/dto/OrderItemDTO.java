package com.vgarcia.marketplace.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemDTO(
        Long id,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        @NotNull(message = "El ID del producto es obligatorio")
        Long productId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer quantity
) {
}
