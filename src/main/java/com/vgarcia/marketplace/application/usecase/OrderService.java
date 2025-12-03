package com.vgarcia.marketplace.application.usecase;

import com.vgarcia.marketplace.application.dto.ImportResultDTO;
import com.vgarcia.marketplace.application.dto.OrderDTO;
import com.vgarcia.marketplace.application.dto.request.UpdateOrderRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public interface OrderService {

    List<OrderDTO> findAll();

    OrderDTO findById(Long id);

    OrderDTO create(OrderDTO orderToCreate);

    OrderDTO update(Long id, UpdateOrderRequest orderToUpdate);

    void deleteById(Long id);

    ImportResultDTO importFromCsv(InputStream inputStream);

    ByteArrayInputStream exportOrdersToPdf();

    ByteArrayInputStream exportOrdersToExcel();

    List<OrderDTO> findOrdersByClientId(Long clientId);
}
