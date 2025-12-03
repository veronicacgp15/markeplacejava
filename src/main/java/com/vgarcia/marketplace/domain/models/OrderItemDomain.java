package com.vgarcia.marketplace.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemDomain(
        Long id,
        Long orderId,
        Long productId,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public OrderItemDomain(Long productId, int quantity, BigDecimal unitPrice) {
        this(
                null,
                null,
                productId,
                quantity,
                unitPrice,
                unitPrice.multiply(new BigDecimal(quantity)),
                null,
                null
        );
    }

}
