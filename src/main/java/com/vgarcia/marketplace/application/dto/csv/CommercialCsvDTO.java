package com.vgarcia.marketplace.application.dto.csv;

import com.vgarcia.marketplace.infrastructure.enums.CurrencyCode;

import java.math.BigDecimal;
import java.util.Optional;

public record CommercialCsvDTO(
        BigDecimal basePrice,
        BigDecimal promotionalPrice,
        boolean isPromotional,
        BigDecimal taxRate,
        CurrencyCode currency
) {
    public static Optional<CommercialCsvDTO> fromCsvRow(String[] row) {

        if (row == null || row.length < 5) {
            return Optional.empty();
        }
        try {
            BigDecimal basePrice = new BigDecimal(row[0]);
            BigDecimal promotionalPrice = (row[1] != null && !row[1].isBlank()) ?
                                            new BigDecimal(row[1]) : null;
            boolean isPromotional = Boolean.parseBoolean(row[2]);
            BigDecimal taxRate = new BigDecimal(row[3]);
            CurrencyCode currency = CurrencyCode.valueOf(row[4].toUpperCase());

            return Optional.of(new CommercialCsvDTO(basePrice, promotionalPrice, isPromotional, taxRate, currency));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
