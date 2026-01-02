package com.vgarcia.marketplace.domain.models;

import com.vgarcia.marketplace.infrastructure.enums.OrderStatus;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDomain(
        Long id,
        Long clientId,
        List<OrderItemDomain> items,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal discount,
        BigDecimal total,
        OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {

    public OrderDomain(Long clientId, List<OrderItemDomain> items, BigDecimal taxRate) {
        this(
                null,
                clientId,
                items,
                calculateSubtotal(items),
                taxRate,
                BigDecimal.ZERO,
                OrderStatus.PENDING,
                null,
                null
        );
    }

    public OrderDomain(Long id, Long clientId, List<OrderItemDomain> newItems, BigDecimal taxRate, OrderStatus status, LocalDateTime createdAt) {
        this(
                id,
                clientId,
                newItems,
                calculateSubtotal(newItems),
                taxRate,
                BigDecimal.ZERO,
                status,
                createdAt,
                null
        );
    }

    private OrderDomain(Long id, Long clientId, List<OrderItemDomain> items, BigDecimal subtotal, BigDecimal taxRate, BigDecimal discount, OrderStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(
                id,
                clientId,
                items,
                subtotal,
                subtotal.multiply(taxRate),
                discount,
                subtotal.add(subtotal.multiply(taxRate)).subtract(discount),
                status,
                createdAt,
                updatedAt
        );
    }

    private static BigDecimal calculateSubtotal(List<OrderItemDomain> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(OrderItemDomain::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public OrderDomain applyDiscount(BigDecimal discountAmount) {
        if (discountAmount == null || discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El descuento no puede ser nulo o negativo.");
        }

        BigDecimal newTotal = this.subtotal.add(this.tax).subtract(discountAmount);

        return new OrderDomain(
                this.id,
                this.clientId,
                this.items,
                this.subtotal,
                this.tax,
                discountAmount,
                newTotal,
                this.status,
                this.createdAt,
                this.updatedAt
        );
    }


}
