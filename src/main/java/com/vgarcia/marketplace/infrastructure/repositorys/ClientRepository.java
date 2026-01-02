package com.vgarcia.marketplace.infrastructure.repositorys;

import com.vgarcia.marketplace.infrastructure.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);

    List<Client> findByLastActivityDateAfter(LocalDateTime date);

     @Query("SELECT c FROM Client c WHERE FUNCTION('YEAR', c.birthDate) = :year")
     List<Client> findByBirthYear(@Param("year") int year);

    @Query("SELECT c.email FROM Client c")
    Set<String> findAllEmails();
}
