package com.bankapp.util;

import com.bankapp.model.Client;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope // Добавляем эту аннотацию (сохраняет состояние между запросами)
public class SessionManager {
    private Client loggedInClient;

    public void login(Client client) {
        this.loggedInClient = client;
    }

    public Client getLoggedInClient() {
        return this.loggedInClient;
    }

    public boolean isLoggedIn() {
        return this.loggedInClient != null;
    }

    public void logout() {
        this.loggedInClient = null;
    }
}