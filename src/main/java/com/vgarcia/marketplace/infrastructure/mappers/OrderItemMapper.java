package com.vgarcia.marketplace.infrastructure.mappers;

import com.vgarcia.marketplace.application.dto.OrderItemDTO;
import com.vgarcia.marketplace.domain.models.OrderItemDomain;
import com.vgarcia.marketplace.infrastructure.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface OrderItemMapper {

    // --- Entity -> Domain ---
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "subtotal", target = "lineTotal")
    OrderItemDomain toDomain(OrderItem entity);
    List<OrderItemDomain> toDomainList(List<OrderItem> entities);

    // --- Domain -> Entity ---
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(source = "lineTotal", target = "subtotal")
    OrderItem toEntity(OrderItemDomain domain);

    // --- Domain -> DTO (Respuesta) ---
    OrderItemDTO toDto(OrderItemDomain domain);
    List<OrderItemDTO> toDtoList(List<OrderItemDomain> domains);

    // --- DTO -> Domain (Petición) ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    @Mapping(target = "lineTotal", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OrderItemDomain toDomain(OrderItemDTO dto);
}
