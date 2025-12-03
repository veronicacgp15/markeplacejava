package com.vgarcia.marketplace.application.dto.csv;

import com.vgarcia.marketplace.infraestructure.enums.OrderStatus;

public record OrderCsvDTO (
        Long clientId,
        OrderStatus status
){
    public static OrderCsvDTO fromCsvRow(String[] row) {
        if (row == null || row.length == 0 || row[0] == null || row[0].isBlank()) {
            return null;
        }

        try {
            Long clientId = Long.parseLong(row[0].trim());
            OrderStatus status = null;

            if (row.length > 1 && row[1] != null && !row[1].isBlank()) {
                status = OrderStatus.valueOf(row[1].trim().toUpperCase());
            }

            return new OrderCsvDTO(clientId, status);

        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
