package com.vgarcia.marketplace.domain.exception;

public class CommercialNotFoundException extends RuntimeException {
    public CommercialNotFoundException(Long id) {
        super("El registro comercial con ID " + id + " no fue encontrado.");
    }
}
