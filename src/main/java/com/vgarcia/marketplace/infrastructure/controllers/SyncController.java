package com.vgarcia.marketplace.infrastructure.controllers;

import com.vgarcia.marketplace.application.dto.ProductSyncResponse;
import com.vgarcia.marketplace.application.services.ExternalProductIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncController {

    private final ExternalProductIntegrationService integrationService;

    @PostMapping("/external-products")
    public ResponseEntity<ProductSyncResponse> syncProducts() {
        ProductSyncResponse response = integrationService.syncExternalProducts();
        return ResponseEntity.ok(response);
    }
}
