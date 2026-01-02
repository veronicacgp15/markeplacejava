package com.vgarcia.marketplace.domain.models.exeption;

public class CircularCategoryReferenceException extends RuntimeException {
    public CircularCategoryReferenceException(String message) {
        super(message);
    }
}
