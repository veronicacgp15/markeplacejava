package com.vgarcia.marketplace.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CommercialRequestDTO(
        @NotNull(message = "El precio base es obligatorio.")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio base debe ser mayor que cero.")
        BigDecimal basePrice,

        @NotNull(message = "La tasa de impuesto es obligatoria.")
        @DecimalMin(value = "0.0", message = "La tasa de impuesto no puede ser negativa.")
        BigDecimal taxRate
) {
}
