package com.vgarcia.marketplace.domain.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long id) {
        super("Categoría no encontrada con ID: " + id);
    }

    public CategoryNotFoundException(Long id, String message) {
        super(message + " ID: " + id);
    }
}
