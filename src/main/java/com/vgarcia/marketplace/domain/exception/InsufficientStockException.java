package com.vgarcia.marketplace.domain.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long resourceId, int quantityRequested, int stockAvailable) {
        super(String.format(
                "No hay suficiente stock para el recurso con ID %d. Solicitado: %d, Disponible: %d",
                resourceId,
                quantityRequested,
                stockAvailable
        ));
    }
}
