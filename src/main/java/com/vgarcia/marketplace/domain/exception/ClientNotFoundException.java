package com.vgarcia.marketplace.domain.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(Long id) {
        super("Client con ID " + id + " no fue encontrado.");
    }
}
