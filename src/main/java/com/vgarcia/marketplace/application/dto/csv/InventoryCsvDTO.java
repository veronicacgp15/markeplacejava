package com.vgarcia.marketplace.application.dto.csv;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public record InventoryCsvDTO(
        Long productId,
        int currentStock,
        String warehouseLocation
) {

    private static final Logger log = LoggerFactory.getLogger(InventoryCsvDTO.class);


    public static Optional<InventoryCsvDTO> fromCsvRow(String[] row, int rowNum) {
        if (row == null || row.length < 3) {
            log.warn("Fila de inventario #{} ignorada: número de columnas insuficiente (esperado: 3, encontrado: {}).", rowNum, row != null ? row.length : 0);
            return Optional.empty();
        }

        try {
            Long productId = Long.parseLong(row[0].trim());
            int currentStock = Integer.parseInt(row[1].trim());
            String warehouseLocation = row[2].trim();

            if (currentStock < 0) {
                log.warn("Fila de inventario #{} ignorada: el stock '{}' no puede ser negativo.", rowNum, currentStock);
                return Optional.empty();
            }

            return Optional.of(new InventoryCsvDTO(productId, currentStock, warehouseLocation));

        } catch (NumberFormatException e) {
            log.error("Fila de inventario #{} ignorada: error al parsear un número. productId='{}', currentStock='{}'.", rowNum, row[0], row[1], e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Fila de inventario #{} ignorada: error inesperado al procesar la fila.", rowNum, e);
            return Optional.empty();
        }
    }
}
