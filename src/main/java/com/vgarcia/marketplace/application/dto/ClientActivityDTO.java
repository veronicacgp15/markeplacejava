package com.vgarcia.marketplace.application.dto;

import java.util.List;

public record ClientActivityDTO(
        ClientDTO clientDetails,
        List<OrderSummaryDTO> orderHistory
) {
}
