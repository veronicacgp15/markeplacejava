package com.vgarcia.marketplace.application.services;

import com.opencsv.bean.CsvToBeanBuilder;
import com.vgarcia.marketplace.application.dto.*;
import com.vgarcia.marketplace.application.dto.csv.ClientCsvDTO;
import com.vgarcia.marketplace.application.helpers.GenericExportHelper;
import com.vgarcia.marketplace.application.helpers.GenericImportHelper;
import com.vgarcia.marketplace.application.usecase.ClientService;
import com.vgarcia.marketplace.application.usecase.OrderService;
import com.vgarcia.marketplace.domain.exception.ClientNotFoundException;
import com.vgarcia.marketplace.domain.models.ClientDomain;
import com.vgarcia.marketplace.domain.ports.ClientPersistencePort;
import com.vgarcia.marketplace.infrastructure.entities.Client;
import com.vgarcia.marketplace.infrastructure.mappers.ClientMapper;
import com.vgarcia.marketplace.infrastructure.mappers.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

import static com.vgarcia.marketplace.infrastructure.utils.Constans.*;

@Component
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientPersistencePort clientPersistencePort;
    private final ClientMapper clientMapper;
    private final GenericImportHelper genericImportHelper;
    private final GenericExportHelper exportHelper;
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> findAll() {
        List<ClientDomain> clients = clientPersistencePort.findAll();
        return clientMapper.toDtoList(clients);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDTO findById(Long id) {
        return clientPersistencePort.findById(id)
                .map(clientMapper::toDto)
                .orElseThrow(() -> new ClientNotFoundException(id));
    }

    @Override
    @Transactional
    public ClientDTO create(ClientDTO clientToCreate) {
        clientPersistencePort.findByEmail(clientToCreate.email()).ifPresent(c -> {
            throw new IllegalStateException(EMAIL_EXISTS + c.email());
        });
        ClientDomain domainToSave = clientMapper.toDomain(clientToCreate);
        ClientDomain savedDomain = clientPersistencePort.save(domainToSave);
        return clientMapper.toDto(savedDomain);
    }

    @Override
    @Transactional
    public ClientDTO edit(Long id, ClientDTO clientToUpdate) {
        Client existingEntity = clientPersistencePort.findEntityById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        clientMapper.updateEntityFromDto(clientToUpdate, existingEntity);

        ClientDomain updatedDomain = clientPersistencePort.save(clientMapper.toDomain(existingEntity));

        return clientMapper.toDto(updatedDomain);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!clientPersistencePort.existsById(id)) {
            throw new ClientNotFoundException(id);
        }
        clientPersistencePort.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientActivityDTO getActivityHistory(Long clientId) {
        ClientDTO clientDto = this.findById(clientId);
        List<OrderDTO> clientOrders = orderService.findOrdersByClientId(clientId);
        List<OrderSummaryDTO> orderSummaries = clientOrders.stream()
                .map(orderMapper::toSummaryDto)
                .toList();
        return new ClientActivityDTO(clientDto, orderSummaries);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientStatsDTO getClientStats(Long clientId) {
        ClientDomain clientDomain = clientPersistencePort.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        // Asumiendo que estos métodos existen en tu ClientDomain
        long yearsOfAntiquity = clientDomain.getYearsOfAntiquity();
        long daysSinceLegacyRegistration = clientDomain.getDaysSinceLegacyRegistration();

        return new ClientStatsDTO(clientId, yearsOfAntiquity, daysSinceLegacyRegistration);
    }

    @Override
    @Transactional
    public ImportResultDTO importFromCsv(InputStream inputStream) {
        log.info("Iniciando importación de clientes desde CSV");
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            List<ClientCsvDTO> clientCsvDtos = new CsvToBeanBuilder<ClientCsvDTO>(reader)
                    .withType(ClientCsvDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            if (clientCsvDtos.isEmpty()) {
                return new ImportResultDTO(0, 0, 0, List.of());
            }

            Set<String> existingEmails = clientPersistencePort.findAllEmails();
            List<ClientDomain> clientsToSave = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (int i = 0; i < clientCsvDtos.size(); i++) {
                ClientCsvDTO dto = clientCsvDtos.get(i);
                int rowNum = i + 2;

                if (dto.getEmail() == null || dto.getEmail().isBlank()) {
                    errors.add("Fila " + rowNum + ": El email es obligatorio.");
                    continue;
                }
                if (existingEmails.contains(dto.getEmail())) {
                    errors.add("Fila " + rowNum + ": El email '" + dto.getEmail() + "' ya está registrado.");
                    continue;
                }


                clientsToSave.add(clientMapper.fromCsvDtoToDomain(dto));
                existingEmails.add(dto.getEmail());
            }

            if (!clientsToSave.isEmpty()) {
                clientPersistencePort.saveAll(clientsToSave);
            }

            log.info("Importación CSV completada. Filas procesadas: {}, Clientes creados: {}, Errores: {}",
                    clientCsvDtos.size(), clientsToSave.size(), errors.size());

            return new ImportResultDTO(clientCsvDtos.size(), clientsToSave.size(), errors.size(), errors);

        } catch (Exception e) {
            log.error("Fallo crítico durante la importación del CSV de clientes.", e);
            throw new RuntimeException("No se pudo procesar el archivo CSV. Causa: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportClientsToPdf() {
        List<ClientDTO> clients = this.findAll();
        return exportHelper.exportToPdf(LISTADO_DE_CLIENTES, getPdfHeaders(), clients, getClientMappersForPdf());
    }

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportClientsToExcel() {
        List<ClientDTO> clients = this.findAll();
        return exportHelper.exportToExcel("Clientes", getExcelHeaders(), clients, getClientMappersForExcel());
    }

    private List<String> getPdfHeaders() {
        return List.of("ID", "Nombre", "Apellido", "Email", "Fec. Nac.");
    }

    private List<String> getExcelHeaders() {
        return List.of("ID", "Nombre", "Apellido", "Email", "Teléfono", "Dirección", "Fecha de Nacimiento");
    }

    private List<Function<ClientDTO, String>> getClientMappersForPdf() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return List.of(
                client -> Objects.toString(client.id(), ""),
                ClientDTO::name,
                ClientDTO::lastName,
                ClientDTO::email,
                client -> client.birthDate() != null ? client.birthDate().format(formatter) : ""
        );
    }

    private List<Function<ClientDTO, String>> getClientMappersForExcel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return List.of(
                client -> Objects.toString(client.id(), ""),
                ClientDTO::name,
                ClientDTO::lastName,
                ClientDTO::email,
                ClientDTO::phoneNumber,
                ClientDTO::address,
                client -> client.birthDate() != null ? client.birthDate().format(formatter) : ""
        );
    }



}
