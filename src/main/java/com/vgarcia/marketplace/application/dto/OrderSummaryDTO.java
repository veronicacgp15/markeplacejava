package com.vgarcia.marketplace.application.dto;

import com.vgarcia.marketplace.infrastructure.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryDTO(
        Long orderId,
        LocalDateTime orderDate,
        OrderStatus status,
        BigDecimal totalAmount
){
}
