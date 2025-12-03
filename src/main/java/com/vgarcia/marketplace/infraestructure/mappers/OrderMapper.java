package com.vgarcia.marketplace.infraestructure.mappers;

import com.vgarcia.marketplace.application.dto.OrderDTO;
import com.vgarcia.marketplace.application.dto.OrderSummaryDTO;
import com.vgarcia.marketplace.domain.models.OrderDomain;
import com.vgarcia.marketplace.infraestructure.entitys.Order;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, ClientMapper.class })
public interface OrderMapper {
    // --- Entity -> Domain ---
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "totalAmount", target = "total")
    @Mapping(source = "orderDate", target = "createdAt")
    @Mapping(source = "metadata.updatedAt", target = "updatedAt")
    OrderDomain toDomain(Order entity);

    List<OrderDomain> toDomainList(List<Order> entities);

    // --- Domain -> Entity ---
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    @Mapping(source = "total", target = "totalAmount")
    @Mapping(source = "createdAt", target = "orderDate")
    Order toEntity(OrderDomain domain);

    // --- Domain -> DTO ---
    @Mapping(source = "createdAt", target = "orderDate")
    OrderDTO toDto(OrderDomain domain);

    List<OrderDTO> toDtoList(List<OrderDomain> domains);

    // --- DTO -> Domain ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "tax", ignore = true)
    @Mapping(target = "discount", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OrderDomain toDomain(OrderDTO dto);

    // --- DTO -> SummaryDTO ---
    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "total", target = "totalAmount")
    OrderSummaryDTO toSummaryDto(OrderDTO orderDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    @Mapping(source = "total", target = "totalAmount")
    @Mapping(source = "createdAt", target = "orderDate")
    void updateEntityFromDomain(OrderDomain domain, @MappingTarget Order entity);

}
