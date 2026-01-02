package com.vgarcia.marketplace.infrastructure.repositorys;

import com.vgarcia.marketplace.infrastructure.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    List<Category> findByParentCategoryIsNull();

    List<Category> findByParentCategoryId(Long parentId);

    @Query("SELECT c.name FROM Category c")
    Set<String> findAllNames();

    @Query("SELECT c.id FROM Category c")
    Set<Long> findAllIds();


}
