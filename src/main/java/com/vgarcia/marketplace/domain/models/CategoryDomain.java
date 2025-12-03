package com.vgarcia.marketplace.domain.models;

import java.util.Set;

public record CategoryDomain (
        Long id,
        String name,
        String description,
        Long parentCategoryId,
        Set<CategoryDomain> subCategories,
        Integer productCount,
        MetaDataDomain metadata
){
    public CategoryDomain(String name, String description, Long parentCategoryId) {
        this(null, name, description, parentCategoryId, Set.of(), 0, null);
    }
}
