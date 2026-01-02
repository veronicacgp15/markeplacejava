package com.vgarcia.marketplace.infrastructure.adaptars;

import com.vgarcia.marketplace.application.dto.ExternalProductDTO;
import com.vgarcia.marketplace.domain.ports.ExternalApiClientPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
public class MockExternalApiClientAdapter implements ExternalApiClientPort {
    @Override
    public List<Long> fetchAllProductIds() {
        log.info("[API EXTERNA] Solicitando lista de IDs...");

        return List.of(100L, 101L, 102L, 103L, 104L, 105L, 106L, 107L, 108L, 109L);
    }

    @Override
    public ExternalProductDTO getProductDetail(Long id) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("[API EXTERNA] Descargando detalle del producto ID: {} en el hilo: {}", id, Thread.currentThread());

        return new ExternalProductDTO(
                id,
                "EXT-" + id,
                "Producto Externo " + id,
                "Descripción importada",
                BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(10, 500)),
                ThreadLocalRandom.current().nextInt(1, 100),
                "General"
        );
    }
}
