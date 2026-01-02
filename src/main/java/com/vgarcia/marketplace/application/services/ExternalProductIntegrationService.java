package com.vgarcia.marketplace.application.services;


import com.vgarcia.marketplace.application.dto.ExternalProductDTO;
import com.vgarcia.marketplace.domain.models.ProductDomain;
import com.vgarcia.marketplace.domain.ports.ExternalApiClientPort;
import com.vgarcia.marketplace.domain.ports.ProductPersistencePort;
import com.vgarcia.marketplace.infrastructure.mappers.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalProductIntegrationService {

    private final ExternalApiClientPort externalApiClient;
    private final ProductPersistencePort productPersistencePort;
    private final ProductMapper productMapper;


    public void syncExternalProducts() {
        log.info("Iniciando sincronización masiva con proveedor externo...");

        List<Long> externalIds = externalApiClient.fetchAllProductIds();
        log.info("Se encontraron {} productos externos para procesar.", externalIds.size());

        if (externalIds.isEmpty()) {
            return;
        }

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            List<CompletableFuture<Void>> futures = externalIds.stream()
                    .map(id -> CompletableFuture.runAsync(() -> processSingleProduct(id), executor))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        log.info("Sincronización finalizada.");
    }


    private void processSingleProduct(Long id) {
        try {

            ExternalProductDTO externalDto = externalApiClient.getProductDetail(id);

            log.info("Procesando producto externo: SKU {}", externalDto.sku());

            // B. Mapeo: Convertimos el objeto externo a nuestro Dominio
            ProductDomain productDomain = productMapper.toDomain(externalDto);

            productPersistencePort.save(productDomain);

        } catch (Exception e) {
            log.error("Error al procesar producto externo ID: {}", id, e);
        }
    }
}
