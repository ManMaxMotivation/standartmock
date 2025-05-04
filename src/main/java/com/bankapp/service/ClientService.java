package com.bankapp.service;

import com.bankapp.model.Client;
import com.bankapp.repository.ClientRepository;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientService {
    private final RestTemplate restTemplate = new RestTemplate();

    public Client register(String fullName, String phone, String username, String password) {
        // Регистрация в auth-service
        String authUrl = "http://localhost:8082/auth/register?fullName=" + fullName
                + "&phone=" + phone
                + "&username=" + username
                + "&password=" + password;
        restTemplate.postForObject(authUrl, null, String.class);

        // Локальная регистрация
        return ClientRepository.save(new Client(fullName, phone, username, password));
    }

    public Optional<Client> login(String username, String password) {
        String authUrl = "http://localhost:8082/auth/login?username=" + username
                + "&password=" + password;
        String authResponse = restTemplate.postForObject(authUrl, null, String.class);

        if (authResponse != null && authResponse.contains("Успешный вход")) {
            return findByUsername(username); // Используем новый метод
        }
        return Optional.empty();
    }

    // NEW: Добавленный метод
    public Optional<Client> findByUsername(String username) {
        return ClientRepository.findByUsername(username);
    }
}