package com.vgarcia.marketplace.application.helpers;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static com.vgarcia.marketplace.infraestructure.utils.Constans.ERROR_PROCESAR_ARCHIVO_CSV;

@Component
public class GenericImportHelper {

    private static final Logger log = LoggerFactory.getLogger(GenericImportHelper.class);

    public <T> List<T> parse(InputStream inputStream,
                             BiFunction<String[], Integer, Optional<T>> rowMapper) {
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReaderBuilder(reader)
                     .withCSVParser(new CSVParserBuilder().withSeparator(',').build())
                     .build()) {

            return IntStream.iterate(1, i -> i + 1)
                    .mapToObj(rowNum -> {
                        try {
                            String[] row = csvReader.readNext();
                            return row == null ? null : new Object[]{row, rowNum};
                        } catch (Exception e) {
                            log.error("Error leyendo la fila #{}", rowNum, e);
                            return null;
                        }
                    })
                    .takeWhile(obj -> obj != null)
                    .skip(1)
                    .map(obj -> rowMapper.apply((String[]) obj[0], (Integer) obj[1]))
                    .flatMap(Optional::stream)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            String errorMessage = ERROR_PROCESAR_ARCHIVO_CSV + e.getMessage();
            log.error(errorMessage, e);
            throw new IllegalArgumentException(errorMessage, e);
        }
    }
}
