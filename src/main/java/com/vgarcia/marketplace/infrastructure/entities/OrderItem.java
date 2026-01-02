package com.vgarcia.marketplace.infrastructure.entities;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "price_per_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerUnit;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return id != null && Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {

        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", orderId=" + (order != null ? order.getId() : "null") +
                ", productId=" + (product != null ? product.getId() : "null") +
                ", quantity=" + quantity +
                ", subtotal=" + subtotal +
                '}';
    }
}
