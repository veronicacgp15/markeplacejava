package com.vgarcia.marketplace.infrastructure.controllers;

import com.vgarcia.marketplace.application.dto.CategoryDTO;
import com.vgarcia.marketplace.application.dto.ImportResultDTO;
import com.vgarcia.marketplace.application.usecase.CategoryService;
import com.vgarcia.marketplace.application.valitadion.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static com.vgarcia.marketplace.infrastructure.utils.Constans.ARCHIVO_VACÍO;
import static com.vgarcia.marketplace.infrastructure.utils.Constans.ERROR_INESPERADO_SERVIDOR;

@RestController
@RequestMapping("/api/marketplace/categories")
@RequiredArgsConstructor
@Tag(name = "Category Management", description = "APIs for managing categories")
public class CategoryController {

    private final CategoryService categoryService;


    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieves a list of all categories.")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.findAll();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    @Operation(summary = "Create a new category", description = "Creates a new category. The ID must be null.")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = categoryService.create(categoryDTO);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }


    @PostMapping(value = "/import-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Import categories from a CSV file", description = "Upload a CSV file to bulk-create categories.")
    public ResponseEntity<ImportResultDTO> importCategoriesFromCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ImportResultDTO(0, 0, 1, List.of(ARCHIVO_VACÍO)));
        }
        try {
            ImportResultDTO result = categoryService.importFromCsv(file.getInputStream());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ImportResultDTO(0, 0, 1, List.of(e.getMessage())));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ImportResultDTO(0, 0, 1, List.of("Error al leer el archivo: " + e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ImportResultDTO(0, 0, 1, List.of(ERROR_INESPERADO_SERVIDOR + e.getMessage())));
        }
    }

    @GetMapping(value = "/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(summary = "Export categories to a PDF file", description = "Generates and returns a PDF file with the list of all categories.")
    public ResponseEntity<InputStreamResource> exportCategoriesToPdf() {
        ByteArrayInputStream bis = categoryService.exportCategoryToPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=categories.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/export/excel")
    @Operation(summary = "Export categories to an Excel file", description = "Generates and returns an Excel file (XLSX) with the list of all categories.")
    public ResponseEntity<InputStreamResource> exportCategoriesToExcel() {
        ByteArrayInputStream bis = categoryService.exportCategoryToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=categories.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(bis));
    }

    // --- Endpoints con rutas de variables (se colocan después de las rutas específicas) ---

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by ID", description = "Retrieves a single category by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = categoryService.findById(id);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing category", description = "Updates the details of an existing category identified by its ID.")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id,
                                                      @Validated(OnUpdate.class) @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.edit(id, categoryDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category by ID", description = "Deletes a category by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found", content = @Content)
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
