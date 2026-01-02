package com.vgarcia.marketplace.application.dto;

import com.vgarcia.marketplace.infrastructure.enums.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO (
        Long id,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal discount,
        BigDecimal total,
        OrderStatus status,
        LocalDateTime orderDate,
        LocalDateTime updatedAt,

        @NotNull(message = "El ID del cliente es obligatorio")
        Long clientId,

        @Valid
        @NotNull(message = "La lista de items no puede ser nula")
        @Size(min = 1, message="El pedido debe tener al menos un item")
        List<OrderItemDTO> items
){
}
