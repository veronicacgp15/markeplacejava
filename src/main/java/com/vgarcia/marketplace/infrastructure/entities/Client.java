package com.vgarcia.marketplace.infrastructure.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Entity
@Table(name = "clients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El email no puede estar vacío.")
    @Email(message = "El formato del email no es válido.")
    private String email;

    @NotBlank(message = "El nombre del cliente no debe estar vacio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo puede contener letras, espacios")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "El apellido del cliente no debe estar vacio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El apellido solo puede contener letras, espacios")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Size(max = 20, message = "El teléfono no debe exceder los 20 caracteres.")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    @Column(name = "last_activity_date", nullable = false)
    private LocalDateTime lastActivityDate;

    @NotNull(message = "La fecha de nacimiento es obligatoria.")
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "last_renewal_date")
    private LocalDate lastRenewalDate;

    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDateTime.now();
        this.lastActivityDate = LocalDateTime.now();
    }

    @Embedded
    @Builder.Default
    private MetaData metadata = new MetaData();


    @Temporal(TemporalType.TIMESTAMP)
    private Date legacyRegistrationDate; // Formato heredado

    public long getDaysSinceLegacyRegistration() {
        if (this.legacyRegistrationDate == null) {
            return 0;
        }
        LocalDate registration = this.legacyRegistrationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return java.time.temporal.ChronoUnit.DAYS.between(registration, LocalDate.now());
    }
}
