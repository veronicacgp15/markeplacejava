package com.vgarcia.marketplace.infrastructure.mappers;

import com.vgarcia.marketplace.application.dto.CommercialDTO;
import com.vgarcia.marketplace.domain.models.CommercialDomain;
import com.vgarcia.marketplace.infrastructure.entities.Commercial;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CommercialMapper {

    // --- DTO -> Domain ---
    CommercialDomain toDomain(CommercialDTO dto);

    // --- Domain -> DTO ---
    CommercialDTO toDto(CommercialDomain domain);

    // --- Entity -> Domain ---
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "metadata.createdAt", target = "createdAt")
    @Mapping(source = "metadata.updatedAt", target = "updatedAt")
    CommercialDomain toDomain(Commercial entity);

    // --- Domain -> Entity ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    Commercial toEntity(CommercialDomain domain);

    // --- Domain -> Entity (Para actualizar existentes) ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    void updateEntityFromDomain(CommercialDomain domain, @MappingTarget Commercial entity);
}
