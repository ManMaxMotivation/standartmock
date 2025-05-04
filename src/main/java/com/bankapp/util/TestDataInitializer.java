package com.bankapp.util;

import com.bankapp.model.Account;
import com.bankapp.model.Client;
import com.bankapp.repository.AccountRepository;
import com.bankapp.repository.ClientRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Component
public class TestDataInitializer implements CommandLineRunner {
    private final AccountRepository accountRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Faker faker = new Faker();
    private final Random random = new Random();
    private final String AUTH_SERVICE_URL = "http://localhost:8082/auth";

    // ✅ Конструктор — без ClientRepository
    public TestDataInitializer(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("📌 Генерация тестовых данных...");

        for (int i = 0; i < 10; i++) {
            String fullName = faker.name().fullName();
            String phone = "+79" + (random.nextInt(900000000) + 100000000);
            String username = "user" + (i + 1);
            String password = "pass" + (i + 1);

            // Регистрация в auth-сервисе
            String registerUrl = AUTH_SERVICE_URL + "/register?fullName=" + fullName
                    + "&phone=" + phone
                    + "&username=" + username
                    + "&password=" + password;
            restTemplate.postForObject(registerUrl, null, String.class);

            // ✅ Локальное сохранение — вызов static-метода напрямую
            Client client = new Client(fullName, phone, username, password);
            ClientRepository.save(client);
            System.out.println("✅ Создан клиент: " + fullName + " (" + phone + ") | Логин: " + username + ", Пароль: " + password);

            // Счета
            int accountCount = random.nextInt(3) + 1;
            for (int j = 0; j < accountCount; j++) {
                Account account = new Account();
                double initialBalance = random.nextInt(9000) + 1000;
                account.setBalance(initialBalance);
                client.getAccounts().add(account);
                accountRepository.save(account);
                System.out.println("  ➕ Счет: " + account.getAccountNumber() + " | Карта: " + account.getCardNumber() + " | Баланс: " + initialBalance + "₽");
            }
        }

        System.out.println("🎉 Генерация тестовых данных завершена!");
    }
}
