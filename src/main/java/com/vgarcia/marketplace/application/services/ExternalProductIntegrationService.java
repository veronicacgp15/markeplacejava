package com.vgarcia.marketplace.application.services;


import com.vgarcia.marketplace.application.dto.ExternalProductDTO;
import com.vgarcia.marketplace.application.dto.ProductSyncResponse;
import com.vgarcia.marketplace.domain.models.ProductDomain;
import com.vgarcia.marketplace.domain.ports.ExternalApiClientPort;
import com.vgarcia.marketplace.domain.ports.ProductPersistencePort;
import com.vgarcia.marketplace.infrastructure.mappers.ProductMapper;
import com.vgarcia.marketplace.infrastructure.utils.Constans;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.vgarcia.marketplace.infrastructure.utils.Constans.NO_SE_ENCONTRARON_PRODUCTOS_EXTERNOS;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalProductIntegrationService {

    private final ExternalApiClientPort externalApiClient;
    private final ProductPersistencePort productPersistencePort;
    private final ProductMapper productMapper;

    public ProductSyncResponse syncExternalProducts() {

        List<Long> externalIds = externalApiClient.fetchAllProductIds();

        if (externalIds.isEmpty()) {
            return new ProductSyncResponse(0, 0, 0, 0, List.of(), List.of(),
                    NO_SE_ENCONTRARON_PRODUCTOS_EXTERNOS);
        }

        AtomicInteger createdCounter = new AtomicInteger(0);
        AtomicInteger updatedCounter = new AtomicInteger(0);
        AtomicInteger failureCounter = new AtomicInteger(0);

        List<String> logs = Collections.synchronizedList(new ArrayList<>());
        List<String> errorMessages = Collections.synchronizedList(new ArrayList<>());

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            List<CompletableFuture<Void>> futures = externalIds.stream()
                    .map(id -> CompletableFuture.runAsync(() ->
                            processSingleProduct(id, createdCounter,
                                    updatedCounter, failureCounter, logs, errorMessages), executor)
                    )
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        return new ProductSyncResponse(
                externalIds.size(),
                createdCounter.get(),
                updatedCounter.get(),
                failureCounter.get(),
                new ArrayList<>(logs),
                new ArrayList<>(errorMessages),
                "Proceso de sincronización finalizado."
        );
    }

    private void processSingleProduct(Long id,
                                      AtomicInteger created,
                                      AtomicInteger updated,
                                      AtomicInteger failure,
                                      List<String> logs,
                                      List<String> errorMessages) {
        try {
            ExternalProductDTO externalDto = externalApiClient.getProductDetail(id);

            boolean exists = productPersistencePort.existsBySku(externalDto.sku());

            if (exists) {
                updated.incrementAndGet();
                logs.add("Producto ID " + id + " (SKU: " + externalDto.sku() + "): Registro ya ejecutado.");
            } else {
                ProductDomain productDomain = productMapper.toDomain(externalDto);

                productPersistencePort.save(productDomain);

                created.incrementAndGet();
                logs.add("Producto ID " + id + " (SKU: " + externalDto.sku() + "): Nuevo registro creado.");
            }

        } catch (Exception e) {
            failure.incrementAndGet();
            errorMessages.add("ID " + id + " falló: " + e.getMessage());
        }
    }
}
