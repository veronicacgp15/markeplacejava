package com.vgarcia.marketplace.infrastructure.repositorys;

import com.vgarcia.marketplace.infrastructure.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySku(String sku);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.commercial " +
            "LEFT JOIN FETCH p.inventory " +
            "WHERE p.id = :id")
    Optional<Product> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.commercial " +
            "LEFT JOIN FETCH p.inventory")
    List<Product> findAllWithDetails();

    @Query("SELECT p.sku FROM Product p")
    Set<String> findAllSkus();

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.inventory " +
            "LEFT JOIN FETCH p.commercial " +
            "WHERE p.id IN :productIds")
    List<Product> findByIdsWithDetails(@Param("productIds") Set<Long> productIds);

    @Query(value = "SELECT p FROM Product p LEFT JOIN FETCH p.inventory LEFT JOIN FETCH p.commercial",
            countQuery = "SELECT count(p) FROM Product p")
    Page<Product> findAllWithDetails(Pageable pageable);

}
