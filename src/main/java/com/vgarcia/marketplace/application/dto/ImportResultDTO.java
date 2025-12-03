package com.vgarcia.marketplace.application.dto;

import java.util.List;

public record ImportResultDTO(
        int totalRecords,
        int successCount,
        int failureCount,
        List<String> errors
){
}
