package com.vgarcia.marketplace.infrastructure.controllers;

import com.vgarcia.marketplace.application.dto.ClientDTO;
import com.vgarcia.marketplace.application.dto.ImportResultDTO;
import com.vgarcia.marketplace.application.usecase.ClientService;
import com.vgarcia.marketplace.application.valitadion.OnCreate;
import com.vgarcia.marketplace.application.valitadion.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.List;

import static com.vgarcia.marketplace.infrastructure.utils.Constans.ARCHIVO_VACÍO;
import static com.vgarcia.marketplace.infrastructure.utils.Constans.ERROR_INESPERADO_SERVIDOR;

@RestController
@RequestMapping("/api/marketplace/clients")
@RequiredArgsConstructor
@Tag(name = "Client Management", description = "APIs for creatin")
public class ClientController {

    private final ClientService clientService;

    @Operation(summary = "Get all clients")
    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @Operation(summary = "Get a client by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    @Operation(summary = "Create a new client")
    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@Validated(OnCreate.class)
                                                      @RequestBody ClientDTO clientDTO) {
        ClientDTO createdClient = clientService.create(clientDTO);
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing client")
    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id,
                                                  @Validated(OnUpdate.class)
                                                  @RequestBody ClientDTO clientDTO) {
        return ResponseEntity.ok(clientService.edit(id, clientDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/import-csv", consumes = "multipart/form-data")
    public ResponseEntity<ImportResultDTO>
                    importClientsFromCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ImportResultDTO(0, 0, 1, List.of(ARCHIVO_VACÍO)));
        }
        try {

            ImportResultDTO result = clientService.importFromCsv(file.getInputStream());

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(new ImportResultDTO(0, 0, 1, List.of(e.getMessage())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ImportResultDTO(0, 0, 1, List.of(ERROR_INESPERADO_SERVIDOR + e.getMessage())));
        }
    }

    @GetMapping(value = "/export/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> exportClientsToPdf() {
        ByteArrayInputStream bis = clientService.exportClientsToPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=clientes.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/export/excel")
    @Operation(summary = "Exportar todos los clientes a un archivo Excel")
    public ResponseEntity<InputStreamResource> exportClientsToExcel() {
        ByteArrayInputStream bis = clientService.exportClientsToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=clientes.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType
                        .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(bis));
    }
}
