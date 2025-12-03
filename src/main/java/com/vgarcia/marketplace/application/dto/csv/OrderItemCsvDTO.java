package com.vgarcia.marketplace.application.dto.csv;

import java.util.Map;

public record OrderItemCsvDTO(
        Long orderId,
        Long productId,
        Integer quantity
) {
    public static OrderItemCsvDTO fromCsvRow(Map<String, String> row) {
        return new OrderItemCsvDTO(
                Long.parseLong(row.get("orderId")),
                Long.parseLong(row.get("productId")),
                Integer.parseInt(row.get("quantity"))
        );
    }
}
