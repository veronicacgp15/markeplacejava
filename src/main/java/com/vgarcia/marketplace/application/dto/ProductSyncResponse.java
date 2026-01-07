package com.vgarcia.marketplace.application.dto;

import java.util.List;

public record ProductSyncResponse(
        int totalProductsFound,
        int createdCount,
        int updatedCount,
        int failureCount,
        List<String> logs,
        List<String> errors,
        String statusMessage
) {
}
