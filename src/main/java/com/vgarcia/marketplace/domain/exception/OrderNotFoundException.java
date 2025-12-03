package com.vgarcia.marketplace.domain.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super("La orden con ID " + id + " no fue encontrada.");
    }

}
