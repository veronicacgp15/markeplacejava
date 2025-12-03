package com.vgarcia.marketplace.application.usecase;

import com.vgarcia.marketplace.application.dto.ImportResultDTO;
import com.vgarcia.marketplace.application.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


public interface ProductService {


    ProductDTO create(ProductDTO productDTO);

    ProductDTO update(Long id, ProductDTO productDTO);

    ProductDTO findById(Long id);

    Page<ProductDTO> findAll(Pageable pageable);

    void deleteById(Long id);

    ImportResultDTO importFromCsv(InputStream inputStream);

    ByteArrayInputStream exportToPdf();

    ByteArrayInputStream exportToExcel();
}
