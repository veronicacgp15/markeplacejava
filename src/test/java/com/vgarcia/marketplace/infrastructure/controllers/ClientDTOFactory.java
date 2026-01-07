package com.vgarcia.marketplace.infrastructure.controllers;

import com.vgarcia.marketplace.application.dto.ClientDTO;

import java.time.LocalDate;

public class ClientDTOFactory {
    // Un método que crea un DTO válido para el endpoint de creación
    public static ClientDTO aClientDTOForCreation() {
        return new ClientDTO(
                null,                           // id
                "new.client@example.com",       // email
                "New",                          // name
                "Client",                       // lastName
                "555444333",                    // phoneNumber
                "123 Test Street",              // address
                LocalDate.of(2000, 1, 1), // birthDate
                null,                           // registrationDate
                null,                           // lastActivityDate
                null,                           // lastRenewalDate
                null,                           // yearsOfAntiquity
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
                null,
                null,
                null,
                null,
                null // También debes agregarlo
        );
    }
}
