package com.vgarcia.marketplace.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ClientStatsDTO(
        @Schema(description = "ID del cliente.")
        Long clientId,

        @Schema(description = "Antigüedad del cliente en años, basado en la fecha de registro moderna.")
        long yearsOfAntiquity,

        @Schema(description = "Días transcurridos desde el registro, basado en la fecha de registro legacy (java.util.Date).")
        long daysSinceLegacyRegistration
) {
}
