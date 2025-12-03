package com.vgarcia.marketplace.infraestructure.repositorys;

import com.vgarcia.marketplace.infraestructure.entitys.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository  extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
}
