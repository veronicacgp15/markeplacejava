package com.vgarcia.marketplace.domain.ports;

import com.vgarcia.marketplace.domain.models.ProductDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductPersistencePort {

    ProductDomain save(ProductDomain productDomain);

    void saveAll(List<ProductDomain> products);

    Optional<ProductDomain> findById(Long id);

    Page<ProductDomain> findAll(Pageable pageable);

    void deleteById(Long id);

    boolean existsBySku(String sku);

    boolean existsById(Long id);

    Set<String> findAllSkus();

    List<ProductDomain> findAllWithDetails();

    List<ProductDomain> findByIds(Set<Long> productIds);
}
