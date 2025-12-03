package com.vgarcia.marketplace.application.usecase;

import com.vgarcia.marketplace.application.dto.ClientActivityDTO;
import com.vgarcia.marketplace.application.dto.ClientDTO;
import com.vgarcia.marketplace.application.dto.ClientStatsDTO;
import com.vgarcia.marketplace.application.dto.ImportResultDTO;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public interface ClientService {

    List<ClientDTO> findAll();

    ClientDTO findById(Long id);

    ClientDTO create(ClientDTO clientToCreate);

    ClientDTO edit(Long id, ClientDTO clientToUpdate);

    void deleteById(Long id);

    ImportResultDTO importFromCsv(InputStream inputStream);

    ByteArrayInputStream exportClientsToPdf();

    ByteArrayInputStream exportClientsToExcel();

    ClientActivityDTO getActivityHistory(Long clientId);

    ClientStatsDTO getClientStats(Long clientId);
}
