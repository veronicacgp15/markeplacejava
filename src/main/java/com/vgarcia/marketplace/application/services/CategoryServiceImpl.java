package com.vgarcia.marketplace.application.services;

import com.opencsv.bean.CsvToBeanBuilder;
import com.vgarcia.marketplace.application.dto.CategoryDTO;
import com.vgarcia.marketplace.application.dto.ImportResultDTO;
import com.vgarcia.marketplace.application.dto.csv.CategoryCsvDTO;
import com.vgarcia.marketplace.application.helpers.GenericExportHelper;
import com.vgarcia.marketplace.application.usecase.CategoryService;
import com.vgarcia.marketplace.domain.exception.CategoryCycleException;
import com.vgarcia.marketplace.domain.exception.CategoryNotFoundException;
import com.vgarcia.marketplace.domain.models.CategoryDomain;
import com.vgarcia.marketplace.domain.ports.CategoryPersistencePort;
import com.vgarcia.marketplace.infrastructure.mappers.CategoryMapper;
import lombok.RequiredArgsConstructor;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.vgarcia.marketplace.infrastructure.utils.Constans.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryPersistencePort categoryPersistencePort;
    private final CategoryMapper categoryMapper;
    private final GenericExportHelper exportHelper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        log.info("Buscando todas las categorías");
        List<CategoryDomain> categories = categoryPersistencePort.findAll();
        return categoryMapper.toDtoList(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        log.info("Buscando categoría con ID: {}", id);
        return categoryPersistencePort.findById(id)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Override
    @Transactional
    public CategoryDTO create(CategoryDTO categoryToCreate) {
        log.info("Iniciando creación de categoría con nombre: {}", categoryToCreate.name());

        if (categoryToCreate.parentCategoryId() != null && !categoryPersistencePort.existsById(categoryToCreate.parentCategoryId())) {
            throw new CategoryNotFoundException(categoryToCreate.parentCategoryId(), CATEGORÍA_PADRE_ESPECIFICADA_NO_EXISTE);
        }

        CategoryDomain domainToSave = categoryMapper.toDomain(categoryToCreate);
        CategoryDomain savedDomain = categoryPersistencePort.save(domainToSave);

        log.info("Categoría con ID: {} creada exitosamente", savedDomain.id());
        return categoryMapper.toDto(savedDomain);
    }

    @Override
    @Transactional
    public CategoryDTO edit(Long id, CategoryDTO categoryToUpdate) {
        log.info("Iniciando actualización de la categoría con ID: {}", id);

        return categoryPersistencePort.findById(id)
                .map(existing -> {
                    Optional.ofNullable(categoryToUpdate.parentCategoryId())
                            .filter(newParentId -> !Objects.equals(newParentId,
                                    existing.parentCategoryId()))
                            .ifPresent(newParentId -> {
                                if (newParentId.equals(id)) throw new CategoryCycleException(id,
                                        newParentId, UNA_CATEGORÍA_NO_PUEDE_SER_SU_PROPIO_PADRE);
                                validateCycles(id, newParentId);
                            });

                    return new CategoryDomain(
                            id,
                            categoryToUpdate.name(),
                            categoryToUpdate.description(),
                            categoryToUpdate.parentCategoryId(),
                            existing.subCategories(),
                            existing.productCount(),
                            existing.metadata()
                    );
                })
                .map(categoryPersistencePort::save)
                .map(saved -> {
                    log.info("Categoría con ID: {} actualizada exitosamente", saved.id());
                    return categoryMapper.toDto(saved);
                })
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

     private void validateCycles(Long targetId, Long currentParentId) {
        if (currentParentId == null) {
            return;
        }

        if (currentParentId.equals(targetId)) {
            throw new CategoryCycleException(targetId, currentParentId);
        }

        CategoryDomain parent = categoryPersistencePort.findById(currentParentId)
                .orElseThrow(() -> new CategoryNotFoundException(currentParentId, CATEGORÍA_PADRE_NO_EXISTE));

        validateCycles(targetId, parent.parentCategoryId());
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        log.warn("Iniciando eliminación de la categoría con ID: {}", id);
        if (!categoryPersistencePort.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        categoryPersistencePort.deleteById(id);
        log.info("Categoría con ID: {} eliminada", id);

        return true;
    }


    @Override
    @Transactional
    public ImportResultDTO importFromCsv(InputStream inputStream) {
        log.info("Iniciando importación de categorías desde CSV");
        try (var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            List<CategoryCsvDTO> csvDtoList = new CsvToBeanBuilder<CategoryCsvDTO>(reader)
                    .withType(CategoryCsvDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build().parse();

            if (csvDtoList.isEmpty()) return new ImportResultDTO(0, 0, 0, List.of());

            final Set<String> names = categoryPersistencePort.findAllNames();
            final Set<Long> ids = categoryPersistencePort.findAllIds();

            var results = IntStream.range(0, csvDtoList.size())
                    .mapToObj(i -> {
                        CategoryCsvDTO dto = csvDtoList.get(i);
                        int row = i + 2;

                        return validateField(dto.getName() == null || dto.getName().isBlank(), "Fila " + row + ": Nombre obligatorio")
                                .or(() -> validateField(names.contains(dto.getName()), "Fila " + row + ": La categoría '" + dto.getName() + "' ya existe"))
                                .or(() -> validateField(dto.getParentCategoryId() != null && !ids.contains(dto.getParentCategoryId()),
                                        "Fila " + row + ": Padre con ID '" + dto.getParentCategoryId() + "' no existe"))
                                .map(error -> (Object) error)
                                .orElseGet(() -> {
                                    CategoryDomain domain = categoryMapper.fromCsvToDomain(dto);
                                    names.add(domain.name());
                                    return domain;
                                });
                    })
                    .collect(Collectors.partitioningBy(res -> res instanceof CategoryDomain));

            List<CategoryDomain> toCreate = results.get(true).stream().map(CategoryDomain.class::cast).toList();
            List<String> errors = results.get(false).stream().map(String.class::cast).toList();

            if (!toCreate.isEmpty()) categoryPersistencePort.saveAll(toCreate);

            return new ImportResultDTO(csvDtoList.size(), toCreate.size(), errors.size(), errors);

        } catch (Exception e) {
            throw new RuntimeException(ERROR_ARCHIVO_CSV+ e.getMessage(), e);
        }
    }

    private Optional<String> validateField(boolean isInvalid, String message) {
        return isInvalid ? Optional.of(message) : Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportCategoryToPdf() {
        List<CategoryDTO> categories = this.findAll();
        Map<Long, CategoryDTO> categoryMap = categories.stream()
                .collect(Collectors.toMap(CategoryDTO::id, Function.identity()));
        List<String> headers = List.of("ID", "Nombre", "Descripción", "Categoría Padre");
        List<Function<CategoryDTO, String>> mappers = List.of(
                category -> Objects.toString(category.id(), ""),
                CategoryDTO::name,
                CategoryDTO::description,
                category -> Optional.ofNullable(category.parentCategoryId())
                        .map(categoryMap::get)
                        .map(CategoryDTO::name)
                        .orElse("N/A")
        );
        return exportHelper.exportToPdf("Listado de Categorías", headers, categories, mappers);
    }

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportCategoryToExcel() {
        List<CategoryDTO> categories = this.findAll();
        Map<Long, CategoryDTO> categoryMap = categories.stream()
                .collect(Collectors.toMap(CategoryDTO::id, Function.identity()));
        List<String> headers = List.of("ID", "Nombre", "Descripción", "Categoría Padre", "Cant. Productos");
        List<Function<CategoryDTO, String>> mappers = List.of(
                category -> Objects.toString(category.id(), ""),
                CategoryDTO::name,
                CategoryDTO::description,
                category -> Optional.ofNullable(category.parentCategoryId())
                        .map(categoryMap::get)
                        .map(CategoryDTO::name)
                        .orElse("N/A"),
                category -> Objects.toString(category.productCount(), "0")
        );
        return exportHelper.exportToExcel("Categorías", headers, categories, mappers);
    }
    private String formatErrorMessage(Exception e) {
        return switch (e) {
            case IllegalArgumentException iae -> "Datos inválidos: " + iae.getMessage();
            case NullPointerException npe -> "Error crítico: Valor nulo inesperado";
            case IllegalStateException ise -> "Estado inconsistente: " + ise.getMessage();
            case RuntimeException re when re.getMessage().contains("CSV") -> "Error de formato CSV";
            default -> "Error desconocido: " + e.getClass().getSimpleName();
        };
    }
}
