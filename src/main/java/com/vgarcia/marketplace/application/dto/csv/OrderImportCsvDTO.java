package com.vgarcia.marketplace.application.dto.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class OrderImportCsvDTO {
    @CsvBindByName
    private String groupingId;

    @CsvBindByName
    private Long clientId;

    @CsvBindByName
    private Long productId;

    @CsvBindByName
    private Integer quantity;
}
