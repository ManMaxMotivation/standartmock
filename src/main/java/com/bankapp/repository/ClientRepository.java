package com.bankapp.repository;

import com.bankapp.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    // Метод для поиска клиента по ID
    Optional<Client> findById(Long id);

    // Мы можем оставить поиск по username, если необходимо
    Optional<Client> findByUsername(String username);
}