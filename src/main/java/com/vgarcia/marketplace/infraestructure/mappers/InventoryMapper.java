package com.vgarcia.marketplace.infraestructure.mappers;

import com.vgarcia.marketplace.application.dto.InventoryDTO;
import com.vgarcia.marketplace.domain.models.InventoryDomain;
import com.vgarcia.marketplace.infraestructure.entitys.Inventory;
import org.mapstruct.*;

import java.util.List;


@Mapper(componentModel = "spring",  unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryMapper {
    // --- Entity -> Domain ---
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "metadata.createdAt", target = "createdAt")
    @Mapping(source = "metadata.updatedAt", target = "updatedAt")
    InventoryDomain toDomain(Inventory entity);

    // --- Domain -> Entity ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    Inventory toEntity(InventoryDomain domain);

    List<InventoryDomain> toDomainList(List<Inventory> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromDomain(InventoryDomain domain, @MappingTarget Inventory entity);

    // --- Domain ->
    InventoryDTO toDto(InventoryDomain domain);
}
