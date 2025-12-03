package com.vgarcia.marketplace.infraestructure.mappers;

import com.vgarcia.marketplace.application.dto.ClientDTO;
import com.vgarcia.marketplace.domain.models.ClientDomain;
import com.vgarcia.marketplace.infraestructure.entitys.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import static org.assertj.core.api.Assertions.assertThat;

//Test Unitario
class ClientMapperUnitarioTest {

    private final ClientMapper mapper = Mappers.getMapper(ClientMapper.class);

    private Client clientEntity;
    private ClientDomain clientDomain;
    private ClientDTO clientDTO;

    @BeforeEach
    void setUp() {

        LocalDateTime registrationDate = LocalDateTime.now().minusYears(3);
        LocalDateTime lastActivityDate = LocalDateTime.now().minusDays(10);
        LocalDate lastRenewalDate = LocalDate.now().minusMonths(6);
        Long yearsOfAntiquity = 3L;

        clientEntity = Client.builder()
                .id(1L)
                .name("Jane")
                .lastName("Doe")
                .email("jane.doe@gmail.com")
                .birthDate(LocalDate.of(1995,5,15))
                .lastActivityDate(lastActivityDate)
                .lastRenewalDate(lastRenewalDate)
                .build();

        clientDomain = new ClientDomain(
                1L,
                "jane.doe@example.com",
                "Jane",
                "Doe",
                "555-1234", // phoneNumber
                "123 Main St", // address
                LocalDate.of(1995, 5, 15),
                registrationDate,
                lastActivityDate,
                lastRenewalDate
        );

        clientDTO = new ClientDTO(
                1L,
                "jane.doe@example.com",
                "Jane",
                "Doe",
                "555-1234",
                "123 Main St",
                LocalDate.of(1995, 5, 15),
                registrationDate,
                lastActivityDate,
                lastRenewalDate,
                yearsOfAntiquity
        );
    }

    @Test
    @DisplayName("Debería mapear de Entidad a Dominio correctamente")
    void shouldMapEntityToDomain() {
        // When
        ClientDomain domain = mapper.toDomain(clientEntity);

        // Then
        assertThat(domain).isNotNull();

        assertThat(domain.id()).isEqualTo(clientEntity.getId());
        assertThat(domain.name()).isEqualTo(clientEntity.getName());
        assertThat(domain.email()).isEqualTo(clientEntity.getEmail());
    }

    @Test
    @DisplayName("Deberia mapear de Dominio a DTO,ignorando yearsOfAntiquity")
    void shouldMapDtoToDomain() {
        //When
        ClientDomain domain = mapper.toDomain(clientDTO);

        //Then
        assertThat(domain).isNotNull();
        assertThat(domain.id()).isEqualTo(clientDTO.id());
        assertThat(domain.name()).isEqualTo(clientDTO.name());
        assertThat(domain.registrationDate()).isEqualTo(clientDTO.registrationDate());
    }

    @Test
    @DisplayName("Debería mapear de Dominio a DTO y calcular 'yearsOfAntiquity'")
    void shouldMapDomainToDtoAndCalculateAntiquity() {
        // Given
        long expectedAntiquity = Period.between(clientDomain.registrationDate().toLocalDate(), LocalDate.now()).getYears();

        // When
        ClientDTO dto = mapper.toDto(clientDomain);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(clientDomain.id());
        assertThat(dto.name()).isEqualTo(clientDomain.name());
        assertThat(dto.yearsOfAntiquity()).isEqualTo(expectedAntiquity);
    }

    @Test
    @DisplayName("Debería actualizar un objeto de Dominio desde un DTO, ignorando el ID")
    void shouldUpdateDomainFromDto() {
        // Given
        ClientDTO updateDto = new ClientDTO(
                99L,
                "jane.doe.new@example.com",
                "Jane",
                "Smith",
                "555-5678",
                "456 Oak Ave",
                LocalDate.of(1995, 5, 15),
                clientDomain.registrationDate(),
                LocalDateTime.now(),
                LocalDate.now(),
                null
        );

        ClientDomain domainToUpdate = new ClientDomain(
                1L, "jane.doe@example.com", "Jane", "Doe", "555-1234",
                "123 Main St", LocalDate.of(1995, 5, 15),
                clientDomain.registrationDate(), clientDomain.lastActivityDate(), clientDomain.lastRenewalDate()
        );

    //When
        mapper.updateDomainFromDto(updateDto, domainToUpdate);

    //Then
        assertThat(domainToUpdate.id()).isEqualTo(1L);
        assertThat(domainToUpdate.lastName()).isEqualTo("Doe");
        assertThat(domainToUpdate.email()).isEqualTo("jane.doe@example.com");
    }


}
