package com.vgarcia.marketplace.application.usecase;

import com.vgarcia.marketplace.application.dto.CategoryDTO;
import com.vgarcia.marketplace.application.dto.ImportResultDTO;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public interface CategoryService {

    List<CategoryDTO> findAll();

    CategoryDTO findById(Long id);

    CategoryDTO create(CategoryDTO categoryToCreate);

    CategoryDTO edit(Long id, CategoryDTO categoryToUpdate);

    boolean deleteById(Long id);

    ImportResultDTO importFromCsv(InputStream inputStream);

    ByteArrayInputStream exportCategoryToPdf();

    ByteArrayInputStream exportCategoryToExcel();



}
