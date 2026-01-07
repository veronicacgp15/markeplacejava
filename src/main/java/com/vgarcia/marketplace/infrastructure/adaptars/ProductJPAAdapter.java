package com.vgarcia.marketplace.infrastructure.adaptars;

import com.vgarcia.marketplace.domain.models.ProductDomain;
import com.vgarcia.marketplace.domain.ports.ProductPersistencePort;
import com.vgarcia.marketplace.infrastructure.entities.Category;
import com.vgarcia.marketplace.infrastructure.entities.Product;
import com.vgarcia.marketplace.infrastructure.mappers.CommercialMapper;
import com.vgarcia.marketplace.infrastructure.mappers.InventoryMapper;
import com.vgarcia.marketplace.infrastructure.mappers.ProductMapper;
import com.vgarcia.marketplace.infrastructure.repositorys.CategoryRepository;
import com.vgarcia.marketplace.infrastructure.repositorys.ProductRepository;
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
    private final CategoryRepository categoryRepository;


    private final InventoryMapper inventoryMapper;
    private final CommercialMapper commercialMapper;

    @Override
    @Transactional
    public ProductDomain save(ProductDomain productDomain) {
        Product productEntity = Optional.ofNullable(productDomain.id())
                .flatMap(productRepository::findById)
                .map(existing -> {
                    productMapper.updateEntityFromDomain(productDomain, existing);
                    return existing;
                })
                .orElseGet(() -> productMapper.toEntity(productDomain));

        processInventory(productDomain, productEntity);
        processCommercial(productDomain, productEntity);
        processCategory(productDomain, productEntity);

        return productMapper.toDomain(productRepository.save(productEntity));
    }

    private void processInventory(ProductDomain domain, Product entity) {
        Optional.ofNullable(domain.inventory()).ifPresent(invDomain -> {
            if (entity.getInventory() != null) {
                inventoryMapper.updateEntityFromDomain(invDomain, entity.getInventory());
            } else {
                entity.assignInventory(inventoryMapper.toEntity(invDomain));
            }
        });
    }

    private void processCommercial(ProductDomain domain, Product entity) {
        Optional.ofNullable(domain.commercial()).ifPresent(commDomain -> {
            if (entity.getCommercial() != null) {
                commercialMapper.updateEntityFromDomain(commDomain, entity.getCommercial());
            } else {
                entity.assignCommercialInfo(commercialMapper.toEntity(commDomain));
            }
        });
    }

    private void processCategory(ProductDomain domain, Product entity) {
        Optional.ofNullable(domain.category())
                .ifPresentOrElse(
                        categoryDomain -> {
                            Category category = findCategory(categoryDomain.id(), categoryDomain.name())
                                    .orElseGet(() -> {
                                        Category newCategory = Category.builder()
                                                .name(categoryDomain.name())
                                                .description("Categoría creada automáticamente por integración")
                                                .build();
                                        return categoryRepository.save(newCategory);
                                    });

                            entity.setCategory(category);
                        },
                        () -> {
                            if (entity.getCategory() == null) {
                                throw new IllegalArgumentException("Categoría obligatoria para nuevos productos");
                            }
                        }
                );
    }

    private Optional<Category> findCategory(Long id, String name) {
        if (id != null) return categoryRepository.findById(id);
        if (name != null) return categoryRepository.findByName(name);
        return Optional.empty();
    }



    @Override
    @Transactional
    public void saveAll(List<ProductDomain> products) {
        List<Product> entities = products.stream().map(domain -> {
            Product entity = productMapper.toEntity(domain);
            Optional.ofNullable(domain.id()).ifPresent(entity::setId);

            processCategory(domain, entity);
            processInventory(domain, entity);
            processCommercial(domain, entity);

            return entity;
        }).toList();

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
