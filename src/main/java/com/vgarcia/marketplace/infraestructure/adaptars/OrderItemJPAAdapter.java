package com.vgarcia.marketplace.infraestructure.adaptars;

import com.vgarcia.marketplace.domain.models.OrderItemDomain;
import com.vgarcia.marketplace.domain.ports.OrderItemPersistencePort;
import com.vgarcia.marketplace.infraestructure.entitys.OrderItem;
import com.vgarcia.marketplace.infraestructure.mappers.OrderItemMapper;
import com.vgarcia.marketplace.infraestructure.repositorys.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemJPAAdapter implements OrderItemPersistencePort {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;


    @Override
    @Transactional
    public List<OrderItemDomain> saveAll(List<OrderItemDomain> items) {
        List<OrderItem> entitiesToSave = items.stream()
                .map(orderItemMapper::toEntity)
                .toList();
        List<OrderItem> savedEntities = orderItemRepository.saveAll(entitiesToSave);
        return orderItemMapper.toDomainList(savedEntities);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        orderItemRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemDomain> findByOrderId(Long orderId) {
        return orderItemMapper.toDomainList(orderItemRepository.findByOrderId(orderId));
    }

}
