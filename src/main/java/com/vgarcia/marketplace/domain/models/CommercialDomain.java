package com.vgarcia.marketplace.domain.models;

import com.vgarcia.marketplace.infraestructure.enums.CurrencyCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

public record CommercialDomain(
        Long productId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        BigDecimal basePrice,
        BigDecimal promotionalPrice,
        boolean isPromotional,
        BigDecimal discountPercentage,
        LocalDateTime promoStartDate,
        LocalDateTime promoEndDate,
        BigDecimal taxRate,
        CurrencyCode currency
) {
    private static final int PRICE_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public CommercialDomain {
        basePrice = scale(basePrice);
        promotionalPrice = scale(promotionalPrice);
    }

    public CommercialDomain(BigDecimal basePrice, BigDecimal taxRate) {
        this(
                null, null, null,
                basePrice,
                null,
                false,
                BigDecimal.ZERO,
                null,
                null,
                taxRate,
                CurrencyCode.USD
        );
    }

    private BigDecimal scale(BigDecimal value) {
        return Optional.ofNullable(value)
                .map(p -> p.setScale(PRICE_SCALE, ROUNDING_MODE))
                .orElse(null);
    }


    public BigDecimal calculateSellingPrice() {
        if (isPromoCurrentlyActive()) {
            if (this.promotionalPrice != null &&
                    this.promotionalPrice.compareTo(BigDecimal.ZERO) > 0) {
                return this.promotionalPrice;
            }
            if (this.discountPercentage != null &&
                    this.discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discountFactor = BigDecimal.ONE.subtract(this.discountPercentage.divide(new BigDecimal("100")));
                return this.basePrice.multiply(discountFactor);
            }
        }
        return this.basePrice;
    }


    public boolean isPromoCurrentlyActive() {
        if (!this.isPromotional) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        boolean hasStarted = this.promoStartDate == null || !now.isBefore(this.promoStartDate);
        boolean notExpired = this.promoEndDate == null || !now.isAfter(this.promoEndDate);
        return hasStarted && notExpired;
    }


    public BigDecimal calculateTotalPrice() {
        BigDecimal sellingPrice = this.calculateSellingPrice();
        if (this.taxRate == null || this.taxRate.compareTo(BigDecimal.ZERO) <= 0) {
            return sellingPrice;
        }
        BigDecimal taxFactor = BigDecimal.ONE.add(this.taxRate.divide(new BigDecimal("100")));
        return sellingPrice.multiply(taxFactor).setScale(PRICE_SCALE, ROUNDING_MODE);
    }
}
