package com.vgarcia.marketplace.infrastructure.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private int identification;

    @NotBlank(message = "El nombre del cliente no debe estar vacio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo puede contener letras, espacios")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "El apellido del producto no debe estar vacio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El apellido solo puede contener letras, espacios")
    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El email no puede estar vacío.")
    @Email(message = "El formato del email no es válido.")
    private String email;

    @Size(max = 20, message = "El teléfono no debe exceder los 20 caracteres.")
    @Pattern(regexp = "^(\\+\\d{1,3})?[\\d\\s\\-()]{7,20}$",
            message = "El formato del teléfono no es válido (ej. +569 1234 5678).")
    private String phone;

    @Size(max = 500, message = "La dirección no debe exceder los 500 caracteres.")
    private String direction;

    @NotNull(message = "La fecha de nacimiento es obligatoria.")
    private Date birthdate;
}
