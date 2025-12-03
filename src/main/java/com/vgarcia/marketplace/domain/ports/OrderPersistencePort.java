package com.vgarcia.marketplace.domain.ports;

import com.vgarcia.marketplace.domain.models.OrderDomain;

import java.util.List;
import java.util.Optional;

public interface OrderPersistencePort {

    OrderDomain save(OrderDomain orderDomain);

    Optional<OrderDomain> findById(Long id);

    List<OrderDomain> findAll();

    List<OrderDomain> findByClientId(Long clientId);

    void deleteById(Long id);

    boolean existsById(Long id);


}
