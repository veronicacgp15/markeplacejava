package com.vgarcia.marketplace.infraestructure.adaptars;


import com.vgarcia.marketplace.domain.exception.ClientNotFoundException;
import com.vgarcia.marketplace.domain.exception.ProductNotFoundException;
import com.vgarcia.marketplace.domain.models.OrderDomain;
import com.vgarcia.marketplace.domain.ports.OrderPersistencePort;
import com.vgarcia.marketplace.infraestructure.entitys.Client;
import com.vgarcia.marketplace.infraestructure.entitys.Order;
import com.vgarcia.marketplace.infraestructure.entitys.Product;
import com.vgarcia.marketplace.infraestructure.mappers.OrderMapper;
import com.vgarcia.marketplace.infraestructure.repositorys.ClientRepository;
import com.vgarcia.marketplace.infraestructure.repositorys.OrderRepository;
import com.vgarcia.marketplace.infraestructure.repositorys.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class OrderJPAAdapter implements OrderPersistencePort {


    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final OrderMapper orderMapper;
    @Override
    @Transactional
    public OrderDomain save(OrderDomain orderDomain) {
        if (orderDomain.id() == null) {
            return createNewOrder(orderDomain);
        } else {
            return updateExistingOrder(orderDomain);
        }
    }

    private OrderDomain createNewOrder(OrderDomain orderDomain) {
        Order orderEntity = orderMapper.toEntity(orderDomain);

        Client client = clientRepository.findById(orderDomain.clientId())
                .orElseThrow(() -> new ClientNotFoundException(orderDomain.clientId()));
        orderEntity.setClient(client);

        Order savedEntity = orderRepository.save(orderEntity);
        return orderMapper.toDomain(savedEntity);
    }

    private OrderDomain updateExistingOrder(OrderDomain orderDomain) {
        Order orderEntity = orderRepository.findByIdWithItems(orderDomain.id())
                .orElseThrow(() -> new IllegalStateException("No se encontró la orden a actualizar con ID: " + orderDomain.id()));


        orderMapper.updateEntityFromDomain(orderDomain, orderEntity);


        Order savedEntity = orderRepository.save(orderEntity);
        return orderMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDomain> findById(Long id) {
        return orderRepository.findByIdWithItems(id).map(orderMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDomain> findAll() {
        return orderMapper.toDomainList(orderRepository.findAllWithItems());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDomain> findByClientId(Long clientId) {
        return orderMapper.toDomainList(orderRepository.findByClientIdWithItems(clientId));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return orderRepository.existsById(id);
    }

}
