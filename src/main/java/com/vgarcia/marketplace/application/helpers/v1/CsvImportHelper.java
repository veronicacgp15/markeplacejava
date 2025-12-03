package com.vgarcia.marketplace.application.helpers.v1;

import com.opencsv.CSVParserBuilder;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import com.vgarcia.marketplace.application.dto.ClientDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.vgarcia.marketplace.infraestructure.utils.Constans.*;

@Component
public class CsvImportHelper {

    private static final Logger log = LoggerFactory.getLogger(CsvImportHelper.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public List<ClientDTO> parseClientsFromCsv(InputStream inputStream) {
        try (Reader reader = new InputStreamReader(inputStream,
                                                StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withCSVParser(new CSVParserBuilder().withSeparator(',').build())
                     .build()) {

            List<String[]> allRows = csvReader.readAll();


            return IntStream.range(1, allRows.size())
                    .mapToObj(rowIndex -> {
                        String[] row = allRows.get(rowIndex);
                        return toClientDto(row, rowIndex + 1);
                    })
                    .flatMap(Optional::stream)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error(ERROR_ARCHIVO_CSV, e);
            throw new IllegalArgumentException(ERROR_ARCHIVO_CSV + e.getMessage(), e);
        }
    }

    private Optional<ClientDTO> toClientDto(String[] row, int rowNum) {

        if (row == null || row.length < 6) {
            log.warn(FILA_IGNORADA, rowNum);
            return Optional.empty();
        }

        try {
            String name = row[0].trim();
            String lastName = row[1].trim();
            String email = row[2].trim();
            String phoneNumber = row[3].trim();
            String address = row[4].trim();
            LocalDate birthDate = LocalDate.parse(row[5].trim(), DATE_FORMATTER);

            return Optional.of(new ClientDTO(
                    null, email, name, lastName, phoneNumber, address, birthDate,
                    null, null, null, null,null
            ));

        } catch (DateTimeParseException e) {
            log.error(FILA_ERROR, rowNum, row[5]);
            return Optional.empty();
        } catch (Exception e) {
            log.error(FILA_ERROR, rowNum, String.join(",", row), e);
            return Optional.empty();
        }
    }
}
