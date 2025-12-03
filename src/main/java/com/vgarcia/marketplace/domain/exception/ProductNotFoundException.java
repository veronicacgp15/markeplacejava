package com.vgarcia.marketplace.domain.exception;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(Long id) {
        super("El producto con ID " + id + " no fue encontrado.");
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
