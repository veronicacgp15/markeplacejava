package com.vgarcia.marketplace.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vgarcia.marketplace.application.dto.request.CommercialRequestDTO;
import com.vgarcia.marketplace.application.dto.request.InventoryRequestDTO;
import com.vgarcia.marketplace.application.valitadion.OnCreate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductDTO(
        Long id,

        @NotBlank(groups = OnCreate.class, message = "El SKU no puede estar vacío.")
        @Size(max = 50, message = "El SKU no puede exceder los 50 caracteres.")
        String sku,

        @NotBlank(message = "El nombre no puede estar vacío.")
        @Size(max = 150, message = "El nombre no puede exceder los 150 caracteres.")
        String name,

        String description,

        @NotNull(groups = OnCreate.class, message = "El ID de la categoría es obligatorio.")
        Long categoryId,

        // --- DTOs de Respuesta ---
        CommercialDTO commercial,
        InventoryDTO inventory,

        // --- DTOs de Petición ---
        @NotNull(groups = OnCreate.class, message = "La información comercial es obligatoria.")
        @Valid
        CommercialRequestDTO commercialRequest,

        @NotNull(groups = OnCreate.class, message = "La información de inventario es obligatoria.")
        @Valid
        InventoryRequestDTO inventoryRequest
) {
}
