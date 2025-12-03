package com.vgarcia.marketplace.application.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvIgnore;
import com.vgarcia.marketplace.application.valitadion.OnCreate;
import com.vgarcia.marketplace.application.valitadion.OnUpdate;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClientDTO(
        @CsvIgnore
        @Null(groups = OnCreate.class, message = "El ID debe ser nulo al crear.")
        @NotNull(groups = OnUpdate.class, message = "El ID es obligatorio para la actualización.")
        Long id,

        @CsvBindByName(column = "email", required = true)
        @NotBlank(groups = OnCreate.class, message = "El email no puede estar vacío en el registro.")
        @Email(message = "El formato del email no es válido.")
        String email,

        @CsvBindByName(column = "name", required = true)
        @NotBlank(groups = OnCreate.class, message = "El nombre no debe estar vacío.")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo puede contener letras y espacios.")
        String name,

        @CsvBindByName(column = "lastName", required = true)
        @NotBlank(groups = OnCreate.class, message = "El apellido no debe estar vacío.")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El apellido solo puede contener letras y espacios.")
        String lastName,

        @CsvBindByName(column = "phoneNumber")
        @Size(max = 20, message = "El teléfono no debe exceder los 20 caracteres.")
        String phoneNumber,

        @CsvBindByName(column = "address")
        String address,

        @CsvBindByName(column = "birthDate", required = true)
        @CsvDate("yyyy-MM-dd")
        @NotNull(groups = OnCreate.class, message = "La fecha de nacimiento es obligatoria para el registro.")
        @PastOrPresent(message = "La fecha de nacimiento no puede ser futura.")
        LocalDate birthDate,

        @CsvIgnore
        LocalDateTime registrationDate,

        @CsvIgnore
        LocalDateTime lastActivityDate,

        @CsvIgnore
        LocalDate lastRenewalDate,

        @CsvIgnore
        Long yearsOfAntiquity,

        Long daysSinceLegacyRegistration

)
{
        /* 2 maneras para cargar el csv se puede crear otro
        DTO RECORD lo hicimos de las dos maneras con DTO y utilizando el helper la mas factible es utilizando DTO Separados
        por lo cual se dejo un archivo sin uso llamado @CsvImportHelper.java, en caso de utiizar modificar Repository, Mapper, Service
         */
}
