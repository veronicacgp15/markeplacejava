package com.vgarcia.marketplace.infraestructure.mappers;

import com.vgarcia.marketplace.application.dto.csv.ClientCsvDTO;
import com.vgarcia.marketplace.application.dto.ClientDTO;
import com.vgarcia.marketplace.domain.models.ClientDomain;
import com.vgarcia.marketplace.infraestructure.entitys.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClientMapper {
        // --- Entity -> Domain ---
        @Mapping(source = "metadata.createdAt", target = "registrationDate")
        @Mapping(source = "metadata.updatedAt", target = "lastActivityDate")
        @Mapping(source = "legacyRegistrationDate", target = "legacyRegistrationDate")
        ClientDomain toDomain(Client entity);

        List<ClientDomain> toDomainList(List<Client> entities);


        // --- Domain -> Entity ---
        @Mapping(target = "metadata", ignore = true)
        Client toEntity(ClientDomain domain);


        // --- Domain -> DTO (Record) ---
        @Mapping(source = "yearsOfAntiquity", target = "yearsOfAntiquity")
        @Mapping(source = "daysSinceLegacyRegistration", target = "daysSinceLegacyRegistration")
        ClientDTO toDto(ClientDomain domain);

        List<ClientDTO> toDtoList(List<ClientDomain> domains);


        // --- DTO (Record) -> Domain ---
        ClientDomain toDomain(ClientDTO dto);


        // --- CSV DTO -> Domain ---
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "registrationDate", expression = "java(java.time.LocalDateTime.now())")
        @Mapping(target = "lastActivityDate", expression = "java(java.time.LocalDateTime.now())")
        @Mapping(target = "lastRenewalDate", ignore = true)
        @Mapping(target = "legacyRegistrationDate", expression = "java(new java.util.Date())") // Asignamos la fecha legacy al importar
        ClientDomain fromCsvDtoToDomain(ClientCsvDTO csvDto);


        // --- Update (DTO -> Entity) ---
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "metadata", ignore = true)
        void updateEntityFromDto(ClientDTO dto, @MappingTarget Client entity);
}
