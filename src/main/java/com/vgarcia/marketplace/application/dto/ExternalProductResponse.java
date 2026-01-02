package com.vgarcia.marketplace.application.dto;

import java.math.BigDecimal;

public record ExternalProductResponse(
        Long id,
        String title,
        BigDecimal price,
        String description,
        String category,
        String image
) {
}
