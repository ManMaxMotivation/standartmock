package com.bankapp.service;

import com.bankapp.model.Client;
import com.bankapp.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // Метод регистрации нового клиента
    public Client register(String fullName, String phone, String username, String password) {
        Client client = new Client(fullName, phone, username, password);
        return clientRepository.save(client);
    }

    // Метод входа по логину и паролю
    public Optional<Client> login(String username, String password) {
        return clientRepository.findByUsername(username)
                .filter(client -> client.getPassword().equals(password));
    }
}
