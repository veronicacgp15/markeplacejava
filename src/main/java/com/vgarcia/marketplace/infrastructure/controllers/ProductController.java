package com.vgarcia.marketplace.infrastructure.controllers;

import com.vgarcia.marketplace.application.dto.ImportResultDTO;
import com.vgarcia.marketplace.application.dto.ProductDTO;
import com.vgarcia.marketplace.application.usecase.ProductService;
import com.vgarcia.marketplace.application.valitadion.OnCreate;
import com.vgarcia.marketplace.application.valitadion.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.vgarcia.marketplace.infrastructure.utils.Constans.ARCHIVO_VACÍO;
import static com.vgarcia.marketplace.infrastructure.utils.Constans.ERROR_INESPERADO_SERVIDOR;

@RestController
@RequestMapping("/api/marketplace/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get all products (paginated)")
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(pageable));
    }

    @Operation(summary = "Get a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @Operation(summary = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., category not found, SKU already exists)", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Validated(OnCreate.class) @RequestBody ProductDTO productToCreate) {
        ProductDTO createdProduct = productService.create(productToCreate);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found to update", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input or business rule violation", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Validated(OnUpdate.class) @RequestBody ProductDTO productToUpdate) {
        ProductDTO updatedProduct = productService.update(id, productToUpdate);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Delete a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found to delete", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Import products from a CSV file")
    @PostMapping(value = "/import-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportResultDTO> importProductsFromCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ImportResultDTO(0, 0, 1, List.of(ARCHIVO_VACÍO)));
        }
        try {
            ImportResultDTO result = productService.importFromCsv(file.getInputStream());
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ImportResultDTO(0, 0, 1, List.of("Error al leer el archivo: " + e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ImportResultDTO(0, 0, 1, List.of(ERROR_INESPERADO_SERVIDOR + ": " + e.getMessage())));
        }
    }

    @Operation(summary = "Export all products to a PDF file")
    @GetMapping("/export/pdf")
    public ResponseEntity<InputStreamResource> exportProductsToPdf() {
        ByteArrayInputStream bis = productService.exportToPdf();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "products_" + timestamp + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @Operation(summary = "Export all products to an Excel file")
    @GetMapping("/export/excel")
    public ResponseEntity<InputStreamResource> exportProductsToExcel() {
        ByteArrayInputStream bis = productService.exportToExcel();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "products_" + timestamp + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(bis));
    }
}
