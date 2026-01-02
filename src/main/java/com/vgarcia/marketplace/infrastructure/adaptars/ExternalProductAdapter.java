package com.vgarcia.marketplace.infrastructure.adaptars;

import com.vgarcia.marketplace.application.dto.ExternalProductResponse;
import com.vgarcia.marketplace.domain.models.CategoryDomain;
import com.vgarcia.marketplace.domain.models.CommercialDomain;
import com.vgarcia.marketplace.domain.models.InventoryDomain;
import com.vgarcia.marketplace.domain.models.ProductDomain;
import com.vgarcia.marketplace.domain.ports.CategoryPersistencePort;
import com.vgarcia.marketplace.domain.ports.ProductPersistencePort;
import com.vgarcia.marketplace.infrastructure.enums.CurrencyCode;
import com.vgarcia.marketplace.infrastructure.enums.ProductStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalProductAdapter {

    private final ProductPersistencePort productPersistencePort;
    private final CategoryPersistencePort categoryPersistencePort;

    private final RestClient restClient;
    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();


    public void syncExternalProducts() {
        log.info("Iniciando sincronización de productos externos con Hilos Virtuales...");

        List<Integer> externalIds = IntStream.rangeClosed(1, 20).boxed().toList();

        List<CompletableFuture<Void>> futures = externalIds.stream()
                .map(id -> CompletableFuture.runAsync(
                        () -> fetchAndProcessProduct(id),
                        virtualThreadExecutor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        log.info("Sincronización finalizada.");
    }

    private void fetchAndProcessProduct(Integer id) {
        try {

            ExternalProductResponse externalDto = restClient.get()
                    .uri("/products/" + id)
                    .retrieve()
                    .body(ExternalProductResponse.class);

            if (externalDto == null) return;

            log.info("Procesando producto externo ID: {} en hilo: {}", id, Thread.currentThread());


            String externalSku = "EXT-" + externalDto.id();
            if (productPersistencePort.existsBySku(externalSku)) {
                log.info("El producto con SKU {} ya existe. Saltando...", externalSku);
                return;
            }

            CategoryDomain category = categoryPersistencePort.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("No hay categorías base para asignar productos externos"));

            ProductDomain newProduct = buildProductDomain(externalDto, externalSku, category);

            productPersistencePort.save(newProduct);

            log.info("Producto externo guardado exitosamente: {}", newProduct.name());

        } catch (Exception e) {
            log.error("Error procesando producto externo ID {}: {}", id, e.getMessage());
        }
    }

    private ProductDomain buildProductDomain(ExternalProductResponse dto, String sku,
                                             CategoryDomain category) {

        CommercialDomain commercial = new CommercialDomain(
                null, null, null,
                dto.price(),
                null,
                false,
                BigDecimal.ZERO,
                null, null,
                new BigDecimal("21.00"),
                CurrencyCode.USD
        );

        InventoryDomain inventory = new InventoryDomain(
                null,
                50,
                0,
                "EXT-WAREHOUSE",
                ProductStatus.AVAILABLE,
                0L,
                null,
                null
        );

        return new ProductDomain(
                sku,
                dto.title().length() > 150 ? dto.title().substring(0, 150) : dto.title(),
                dto.description(),
                category,
                commercial,
                inventory,
                null
        );
    }
}
