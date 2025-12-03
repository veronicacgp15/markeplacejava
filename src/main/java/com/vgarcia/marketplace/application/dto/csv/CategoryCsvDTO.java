package com.vgarcia.marketplace.application.dto.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryCsvDTO{

        @CsvBindByName(column = "id", required = false)
        Long id;

        @CsvBindByName(column = "name", required = true)
        String name;

        @CsvBindByName(column = "description")
        String description;

        @CsvBindByName(column = "parentId")
        Long parentCategoryId;

}
