package com.vgarcia.marketplace.domain.ports;

import com.vgarcia.marketplace.domain.models.ClientDomain;
import com.vgarcia.marketplace.infraestructure.entitys.Client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ClientPersistencePort {

    ClientDomain save(ClientDomain client);

    Optional<ClientDomain> findById(Long id);

    Optional<ClientDomain> findByEmail(String email);

    List<ClientDomain> findAll();

    void deleteById(Long id);

    List<ClientDomain> findByLastActivityAfter(LocalDateTime date);
    //nuevo
    Set<String> findAllEmails();
    List<ClientDomain> saveAll(List<ClientDomain> clientsToSave);
    boolean existsById(Long id);

    void updateLastActivityDate(Long clientId);

    Optional<Client> findEntityById(Long id);

}
