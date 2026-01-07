package com.vgarcia.marketplace.application.dto.request;

import com.vgarcia.marketplace.infrastructure.utils.Constans;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

import static com.vgarcia.marketplace.infrastructure.utils.Constans.EL_PRECIO_BASE_ES_OBLIGATORIO;
import static com.vgarcia.marketplace.infrastructure.utils.Constans.LA_TASA_DE_IMPUESTO_ES_OBLIGATORIA;

public record CommercialRequestDTO(
        @NotNull(message = EL_PRECIO_BASE_ES_OBLIGATORIO)
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio base debe ser mayor que cero.")
        BigDecimal basePrice,

        @NotNull(message = LA_TASA_DE_IMPUESTO_ES_OBLIGATORIA)
        @DecimalMin(value = "0.0", message = "La tasa de impuesto no puede ser negativa.")
        BigDecimal taxRate
) {
        }
