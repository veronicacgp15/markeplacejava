package com.vgarcia.marketplace.infraestructure.entitys;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaData {
    
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;
}
