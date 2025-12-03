package com.vgarcia.marketplace.domain.ports;

import com.vgarcia.marketplace.domain.models.CategoryDomain;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryPersistencePort {

    CategoryDomain save(CategoryDomain categoryDomain);
    Optional<CategoryDomain> findById(Long id);
    List<CategoryDomain> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);

    Set<String> findAllNames();
    Set<Long> findAllIds();
    void saveAll(List<CategoryDomain> categories);

}
