package com.vgarcia.marketplace.domain.ports;

import com.vgarcia.marketplace.application.dto.ExternalProductDTO;

import java.util.List;

public interface ExternalApiClientPort {

    List<Long> fetchAllProductIds();
    ExternalProductDTO getProductDetail(Long id);
}
