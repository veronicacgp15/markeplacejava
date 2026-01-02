package com.vgarcia.marketplace.application.dto;

import java.math.BigDecimal;

public record ExternalProductDTO(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String category
) {
}
