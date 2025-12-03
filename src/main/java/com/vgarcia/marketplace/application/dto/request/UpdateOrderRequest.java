package com.vgarcia.marketplace.application.dto.request;

import com.vgarcia.marketplace.application.dto.OrderItemDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateOrderRequest(
        @Valid
        @NotNull(message = "La lista de items no puede ser nula.")
        @Size(min = 1, message = "La orden debe contener al menos un item.")
        List<OrderItemDTO> items
) {
}
