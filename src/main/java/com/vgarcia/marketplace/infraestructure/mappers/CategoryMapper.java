package com.vgarcia.marketplace.infraestructure.mappers;

import com.vgarcia.marketplace.application.dto.CategoryDTO;
import com.vgarcia.marketplace.application.dto.csv.CategoryCsvDTO;
import com.vgarcia.marketplace.domain.models.CategoryDomain;
import com.vgarcia.marketplace.infraestructure.entitys.Category;
import com.vgarcia.marketplace.infraestructure.entitys.Product;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // --- Entity -> Domain ---
    @Mapping(source = "parentCategory.id", target = "parentCategoryId")
    @Mapping(source = "products", target = "productCount", qualifiedByName = "countProducts")
    CategoryDomain toDomain(Category entity);

    List<CategoryDomain> toDomainList(List<Category> entities);

    // --- Domain -> Entity ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentCategory", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    Category toEntity(CategoryDomain domain);

    // --- Domain -> DTO ---
    @Mapping(target = "isRootCategory", expression = "java(domain.parentCategoryId() == null)")
    @Mapping(source = "subCategories", target = "subCategoryIds", qualifiedByName = "domainsToIds")
    CategoryDTO toDto(CategoryDomain domain);

    List<CategoryDTO> toDtoList(List<CategoryDomain> domains);

    // --- DTO -> Domain ---
    @Mapping(target = "subCategories", ignore = true)
    @Mapping(target = "productCount", ignore = true)
    CategoryDomain toDomain(CategoryDTO dto);

    // --- CSV -> Domain ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentCategoryId", source = "parentCategoryId")
    @Mapping(target = "subCategories", ignore = true)
    @Mapping(target = "productCount", ignore = true)
    CategoryDomain fromCsvToDomain(CategoryCsvDTO csvDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentCategory", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    void updateEntityFromDto(CategoryDTO dto, @MappingTarget Category entity);


    // --- Métodos Helper ---
    @Named("countProducts")
    default Integer countProducts(List<Product> products) {
        return products != null ? products.size() : 0;
    }

    @Named("domainsToIds")
    default Set<Long> domainsToIds(Set<CategoryDomain> subCategories) {
        if (subCategories == null) {
            return Set.of();
        }
        return subCategories.stream()
                .map(CategoryDomain::id)
                .collect(Collectors.toSet());
    }
}
