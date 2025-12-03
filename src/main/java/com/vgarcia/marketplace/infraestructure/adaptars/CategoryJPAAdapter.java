package com.vgarcia.marketplace.infraestructure.adaptars;

import com.vgarcia.marketplace.domain.models.CategoryDomain;
import com.vgarcia.marketplace.domain.ports.CategoryPersistencePort;
import com.vgarcia.marketplace.infraestructure.entitys.Category;
import com.vgarcia.marketplace.infraestructure.mappers.CategoryMapper;
import com.vgarcia.marketplace.infraestructure.repositorys.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CategoryJPAAdapter implements CategoryPersistencePort {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    @Override
    public CategoryDomain save(CategoryDomain categoryDomain) {

        Category categoryEntity = categoryMapper.toEntity(categoryDomain);

        if (categoryDomain.parentCategoryId() != null) {
            Category parentEntity = categoryRepository.findById(categoryDomain.parentCategoryId())
                    .orElseThrow(() -> new RuntimeException("La categoría padre con ID " + categoryDomain.parentCategoryId() + " no existe."));

            categoryEntity.setParentCategory(parentEntity);
        } else {
            categoryEntity.setParentCategory(null);
        }

        Category savedEntity = categoryRepository.save(categoryEntity);

        return categoryMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<CategoryDomain> findById(Long id) {
        return categoryRepository.findById(id).map(categoryMapper::toDomain);
    }

    @Override
    public List<CategoryDomain> findAll() {
        return categoryMapper.toDomainList(categoryRepository.findAll());
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public Set<String> findAllNames() {
        return categoryRepository.findAllNames();
    }

    @Override
    public Set<Long> findAllIds() {
        return categoryRepository.findAllIds();
    }

    @Override
    @Transactional
    public void saveAll(List<CategoryDomain> categories) {
        List<Category> categoryEntities = categories.stream()
                .map(domain -> {
                    Category entity = categoryMapper.toEntity(domain);
                    if (domain.parentCategoryId() != null) {
                        categoryRepository.findById(domain.parentCategoryId())
                                .ifPresent(entity::setParentCategory);
                    }
                    return entity;
                })
                .toList();
        categoryRepository.saveAll(categoryEntities);
    }
}
