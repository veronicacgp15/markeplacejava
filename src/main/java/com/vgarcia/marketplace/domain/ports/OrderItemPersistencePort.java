package com.vgarcia.marketplace.domain.ports;

import com.vgarcia.marketplace.domain.models.OrderItemDomain;

import java.util.List;

public interface OrderItemPersistencePort {

    List<OrderItemDomain> saveAll(List<OrderItemDomain> items);

    void deleteById(Long id);

    List<OrderItemDomain> findByOrderId(Long orderId);
}
