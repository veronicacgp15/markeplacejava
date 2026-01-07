package com.vgarcia.marketplace.infrastructure.adaptars;


import com.vgarcia.marketplace.domain.exception.ClientNotFoundException;
import com.vgarcia.marketplace.domain.models.OrderDomain;
import com.vgarcia.marketplace.domain.ports.OrderPersistencePort;
import com.vgarcia.marketplace.infrastructure.entities.Client;
import com.vgarcia.marketplace.infrastructure.entities.Order;
import com.vgarcia.marketplace.infrastructure.mappers.OrderMapper;
import com.vgarcia.marketplace.infrastructure.repositorys.ClientRepository;
import com.vgarcia.marketplace.infrastructure.repositorys.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.vgarcia.marketplace.infrastructure.utils.Constans.NO_SE_ENCONTRÓ_LA_ORDEN_A_ACTUALIZAR_CON_ID;


@Component
@RequiredArgsConstructor
public class OrderJPAAdapter implements OrderPersistencePort {


    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDomain save(OrderDomain orderDomain) {
        Objects.requireNonNull(orderDomain, "orderDomain must not be null");
        return Objects.isNull(orderDomain.id())
                ? createNewOrder(orderDomain)
                : updateExistingOrder(orderDomain);
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
                .orElseThrow(() -> new IllegalStateException(NO_SE_ENCONTRÓ_LA_ORDEN_A_ACTUALIZAR_CON_ID + orderDomain.id()));


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
