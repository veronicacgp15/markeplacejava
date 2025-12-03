package com.vgarcia.marketplace.application.dto;

import com.vgarcia.marketplace.infraestructure.enums.CurrencyCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CommercialDTO(
        Long id,
        BigDecimal basePrice,
        BigDecimal promotionalPrice,
        boolean isPromotional,
        BigDecimal discountPercentage,
        LocalDateTime promoStartDate,
        LocalDateTime promoEndDate,
        BigDecimal taxRate,
        CurrencyCode currency,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {


}
