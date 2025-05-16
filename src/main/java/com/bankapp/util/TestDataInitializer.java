package com.bankapp.util;

import com.bankapp.model.Account;
import com.bankapp.model.Client;
import com.bankapp.repository.AccountRepository;
import com.bankapp.repository.ClientRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;
import java.util.Random;

@Component
public class TestDataInitializer implements CommandLineRunner {
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Faker faker = new Faker();
    private final Random random = new Random();
    private final String AUTH_SERVICE_URL = "http://localhost:8082/auth";

    public TestDataInitializer(AccountRepository accountRepository,
                               ClientRepository clientRepository) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("📌 Генерация тестовых данных...");

        for (int i = 0; i < 10; i++) {
            try {
                String fullName = faker.name().fullName();
                String phone = "+79" + (random.nextInt(900000000) + 100000000);
                String username = "user" + (i + 1);
                String password = "pass" + (i + 1);

                // Проверяем, существует ли клиент с таким username
                Optional<Client> existingClient = clientRepository.findByUsername(username);
                Client client;
                if (existingClient.isPresent()) {
                    // Используем существующего клиента
                    client = existingClient.get();
                    System.out.println("⏭️ Клиент с username " + username + " уже существует, пропускаем регистрацию");
                } else {
                    // Логируем данные перед отправкой
                    System.out.println("Попытка регистрации: " + username + " | Телефон: " + phone);

                    // Регистрация в auth-сервисе
                    String registerUrl = AUTH_SERVICE_URL + "/register?fullName=" + fullName
                            + "&phone=" + phone
                            + "&username=" + username
                            + "&password=" + password;

                    String response = restTemplate.postForObject(registerUrl, null, String.class);
                    System.out.println("Ответ сервера: " + response);

                    // Проверяем, создал ли auth-сервис клиента
                    existingClient = clientRepository.findByUsername(username);
                    if (existingClient.isPresent()) {
                        // Auth-сервис уже создал клиента
                        client = existingClient.get();
                        System.out.println("✅ Клиент " + username + " создан auth-сервисом");
                    } else {
                        // Сохраняем клиента, если auth-сервис его не создал
                        client = new Client(fullName, phone, username, password);
                        client = clientRepository.save(client);
                        System.out.println("✅ Создан клиент: " + fullName + " (" + phone + ") | Логин: " + username + ", Пароль: " + password);
                    }
                }

                // Создание счетов (1-3 счета) для клиента
                int accountCount = random.nextInt(3) + 1;
                for (int j = 0; j < accountCount; j++) {
                    Account account = new Account();
                    double initialBalance = random.nextInt(9000) + 1000;
                    account.setBalance(initialBalance);
                    account.setClient(client);
                    account.setAccountNumber(String.format("%012d", random.nextInt(1000000000) + 1000000000));
                    account.setCardNumber(String.format("%016d", random.nextLong(1000000000000000L, 9999999999999999L)));
                    accountRepository.save(account);
                    System.out.println("  ➕ Счет: " + account.getAccountNumber() + " | Баланс: " + initialBalance + "₽");
                }

            } catch (Exception e) {
                System.err.println("Ошибка при создании пользователя " + (i+1) + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("🎉 Генерация тестовых данных завершена!");
    }
}