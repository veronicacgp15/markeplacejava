package com.vgarcia.marketplace.infraestructure.mappers;

import com.vgarcia.marketplace.application.dto.ProductDTO;
import com.vgarcia.marketplace.application.dto.csv.ProductCsvDTO;
import com.vgarcia.marketplace.domain.models.ProductDomain;
import com.vgarcia.marketplace.infraestructure.entitys.Product;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring", uses = {
        CategoryMapper.class,
        CommercialMapper.class,
        InventoryMapper.class
})
public interface ProductMapper {
    // --- Entity -> Domain ---
    @Mapping(source = "category", target = "category")
    @Mapping(source = "commercial", target = "commercial")
    @Mapping(source = "inventory", target = "inventory")
    @Mapping(source = "metadata.createdAt", target = "metadata.createdAt")
    @Mapping(source = "metadata.updatedAt", target = "metadata.updatedAt")
    ProductDomain toDomain(Product entity);

    List<ProductDomain> toDomainList(List<Product> entities);

    // --- Domain -> Entity ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "commercial", ignore = true)
    @Mapping(target = "inventory", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    @Mapping(target = "similarityVectorJson", ignore = true)
    Product toEntity(ProductDomain domain);

    List<Product> toEntityList(List<ProductDomain> domains);

    // --- Domain -> DTO (Respuesta) ---
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(target = "commercialRequest", ignore = true)
    @Mapping(target = "inventoryRequest", ignore = true)
    ProductDTO toDto(ProductDomain domain);

    // --- DTO -> Domain (Creación/Actualización) ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "commercial", ignore = true)
    @Mapping(target = "inventory", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    ProductDomain toDomain(ProductDTO dto);

    // --- CSV -> Domain ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "commercial", ignore = true)
    @Mapping(target = "inventory", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    ProductDomain fromCsvToDomain(ProductCsvDTO csvDTO);
}
