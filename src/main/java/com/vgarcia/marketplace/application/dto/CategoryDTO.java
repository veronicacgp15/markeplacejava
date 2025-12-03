package com.vgarcia.marketplace.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CategoryDTO(
        Long id,

        @NotBlank(message = "El nombre de la categoría es obligatorio.")
        @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres.")
        String name,

        String description,

        Long parentCategoryId,

        Set<Long> subCategoryIds,

        Integer productCount,

        Boolean isRootCategory,

        MetaDataDTO metadata
                        )
{}
