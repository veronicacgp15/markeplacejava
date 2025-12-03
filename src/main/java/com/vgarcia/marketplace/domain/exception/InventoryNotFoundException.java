package com.vgarcia.marketplace.domain.exception;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(Long id) {
        super("El registro de inventario con ID " + id + " no fue encontrado.");
    }
}
