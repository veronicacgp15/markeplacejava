package com.vgarcia.marketplace.domain.models;


public record ProductDomain(
        Long id,
        String sku,
        String name,
        String description,
        CategoryDomain category,
        CommercialDomain commercial,
        InventoryDomain inventory,
        MetaDataDomain metadata
) {

    public ProductDomain(String sku, String name, String description, CategoryDomain category, CommercialDomain commercial, InventoryDomain inventory, MetaDataDomain metadata) {
        this(null,
                sku,
                name,
                description,
                category,
                commercial,
                inventory,
                metadata);
    }
}
