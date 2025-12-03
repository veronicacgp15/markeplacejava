package com.vgarcia.marketplace.application.dto;

import java.time.LocalDateTime;

public record MetaDataDTO
        (
                String createdBy,
                LocalDateTime createdAt,
                String lasModifiedBy,
                LocalDateTime lastModifiedAt
        )
{}
