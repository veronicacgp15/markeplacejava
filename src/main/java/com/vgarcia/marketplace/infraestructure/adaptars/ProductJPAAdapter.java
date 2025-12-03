package com.vgarcia.marketplace.infraestructure.adaptars;

import com.vgarcia.marketplace.domain.models.ProductDomain;
import com.vgarcia.marketplace.domain.ports.ProductPersistencePort;
import com.vgarcia.marketplace.infraestructure.entitys.Product;
import com.vgarcia.marketplace.infraestructure.mappers.ProductMapper;
import com.vgarcia.marketplace.infraestructure.repositorys.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ProductJPAAdapter implements ProductPersistencePort {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductDomain save(ProductDomain productDomain) {
        Product productEntity = productMapper.toEntity(productDomain);

        productEntity.assignCommercialInfo(productEntity.getCommercial());
        productEntity.assignInventory(productEntity.getInventory());

        Product savedEntity = productRepository.save(productEntity);
        return productMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public void saveAll(List<ProductDomain> products) {
        List<Product> entities = productMapper.toEntityList(products);

        entities.forEach(entity -> {
            entity.assignCommercialInfo(entity.getCommercial());
            entity.assignInventory(entity.getInventory());
        });

        productRepository.saveAll(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDomain> findById(Long id) {
        return productRepository.findByIdWithDetails(id)
                .map(productMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDomain> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDomain> findAllWithDetails() {
        return productMapper.toDomainList(productRepository.findAllWithDetails());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySku(String sku) {

        return productRepository.existsBySku(sku);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {

        return productRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> findAllSkus() {

        return productRepository.findAllSkus();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDomain> findByIds(Set<Long> productIds) {
        List<Product> products = productRepository.findByIdsWithDetails(productIds);
        return productMapper.toDomainList(products);
    }



}
