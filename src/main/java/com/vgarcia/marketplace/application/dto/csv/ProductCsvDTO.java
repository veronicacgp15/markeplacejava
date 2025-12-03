package com.vgarcia.marketplace.application.dto.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductCsvDTO {

    @CsvBindByName(column = "sku", required = true)
    private String sku;

    @CsvBindByName(column = "name", required = true)
    private String name;

    @CsvBindByName
    private String description;

    @CsvBindByName(column = "categoryId", required = true)
    private Long categoryId;

    @CsvBindByName(column = "basePrice")
    private Double basePrice;

    @CsvBindByName(column = "initialStock")
    private Integer initialStock;

    @CsvBindByName(column = "warehouseLocation")
    private String warehouseLocation;
}
