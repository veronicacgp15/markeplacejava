package com.vgarcia.marketplace.infraestructure.controllers;

import com.vgarcia.marketplace.application.dto.ClientDTO;

import java.time.LocalDate;

public class ClientDTOFactory {
    // Un método que crea un DTO válido para el endpoint de creación
    public static ClientDTO aClientDTOForCreation() {
        return new ClientDTO(
                null,
                "new.client@example.com",
                "New",
                "Client",
                "555444333",
                "123 Test Street",
                LocalDate.of(2000, 1, 1),
                null,
                null,
                null,
                null
        );
    }

    // Puedes añadir más métodos para diferentes escenarios
    public static ClientDTO aClientDTOWithCustomEmail(String email) {
        return new ClientDTO(
                null,
                email,
                "New",
                "Client",
                "555444333",
                "123 Test Street",
                LocalDate.of(2000, 1, 1),
                null, null, null, null
        );
    }
}
