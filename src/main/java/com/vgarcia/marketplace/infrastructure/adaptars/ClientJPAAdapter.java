package com.vgarcia.marketplace.infrastructure.adaptars;

import com.vgarcia.marketplace.domain.ports.ClientPersistencePort;
import com.vgarcia.marketplace.domain.models.ClientDomain;
import com.vgarcia.marketplace.infrastructure.entities.Client;
import com.vgarcia.marketplace.infrastructure.mappers.ClientMapper;
import com.vgarcia.marketplace.infrastructure.repositorys.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClientJPAAdapter implements ClientPersistencePort {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientDomain save(ClientDomain client) {

        Client entityToSave = clientMapper.toEntity(client);

        Client savedEntity = clientRepository.save(entityToSave);

        return clientMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ClientDomain> findById(Long id) {
        return clientRepository.findById(id)
                .map(clientMapper::toDomain);
    }

    public Optional<ClientDomain> findByEmail(String email) {
        return clientRepository.findByEmail(email)
                .map(clientMapper::toDomain);
    }

    public List<ClientDomain> findAll() {
        List<Client> entities = clientRepository.findAll();
        return clientMapper.toDomainList(entities);
    }

    public void deleteById(Long id) {
        clientRepository.deleteById(id);
    }

    public List<ClientDomain> findByLastActivityAfter(LocalDateTime date) {
        List<Client> entities = clientRepository.findByLastActivityDateAfter(date);
        return clientMapper.toDomainList(entities);
    }

    @Override
    public Set<String> findAllEmails() {
        return clientRepository.findAllEmails();
    }

    @Override
    public List<ClientDomain> saveAll(List<ClientDomain> clientsToSave) {
        List<Client> clientEntities = clientsToSave.stream()
                .map(clientMapper::toEntity)
                .collect(Collectors.toList());
        List<Client> savedEntities = clientRepository.saveAll(clientEntities);
        return savedEntities.stream()
                .map(clientMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return clientRepository.existsById(id);
    }

    @Override
    public void updateLastActivityDate(Long clientId) {
        clientRepository.findById(clientId).ifPresent(client -> {
            client.setLastActivityDate(LocalDateTime.now());
            clientRepository.save(client);
        });
    }

    @Override
    public Optional<Client> findEntityById(Long id) {
        return clientRepository.findById(id);
    }
}
