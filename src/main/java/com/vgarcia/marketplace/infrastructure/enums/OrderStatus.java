package com.vgarcia.marketplace.infrastructure.enums;

public enum OrderStatus {
    PENDING,      // La orden ha sido creada pero no procesada.
    COMPLETED,    // La orden ha sido completada y entregada.
    SHIPPED,      // La orden ha sido enviada.
    CANCELLED,    // La orden ha sido cancelada.
    PROCESSING
}
