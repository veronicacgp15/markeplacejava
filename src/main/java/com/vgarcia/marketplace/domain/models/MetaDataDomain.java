package com.vgarcia.marketplace.domain.models;

import java.time.LocalDateTime;

public record MetaDataDomain(
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
