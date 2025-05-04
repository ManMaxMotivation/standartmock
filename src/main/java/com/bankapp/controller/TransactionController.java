package com.bankapp.controller;

import com.bankapp.model.Account;
import com.bankapp.model.Client;
import com.bankapp.repository.ClientRepository;
import com.bankapp.service.CustomMetricsService;
import com.bankapp.controller.TimeoutController;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Транзакции", description = "Методы для управления банковскими транзакциями")
public class TransactionController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final CustomMetricsService metricsService;
    private final TimeoutController timeoutController;
    private final MeterRegistry meterRegistry;

    private Client recipientClient;
    private Account recipientAccount;

    @Autowired
    public TransactionController(CustomMetricsService metricsService, TimeoutController timeoutController,
                                 MeterRegistry meterRegistry) {
        this.metricsService = metricsService;
        this.timeoutController = timeoutController;
        this.meterRegistry = meterRegistry;
    }

    private boolean isAuthenticated() {
        try {
            String response = restTemplate.getForObject("http://localhost:8082/auth/isLogged", String.class);
            logger.debug("isAuthenticated response: {}", response);
            return "аутентифицирован".equalsIgnoreCase(response);
        } catch (Exception e) {
            logger.error("Error in isAuthenticated: {}", e.getMessage());
            return false;
        }
    }

    private Optional<Client> getLoggedInClient() {
        try {
            String username = restTemplate.getForObject("http://localhost:8082/auth/user", String.class);
            logger.debug("getLoggedInClient username: {}", username);
            return ClientRepository.findByUsername(username);
        } catch (Exception e) {
            logger.error("Error in getLoggedInClient: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @GetMapping("/clients")
    @Operation(
            summary = "Получение списка клиентов",
            description = "Возвращает список всех клиентов."
    )
    public List<Client> getAllClients() {
        logger.debug("getAllClients called");
        timeoutController.applyTimeout("get_all_clients");
        long startTime = System.nanoTime();

        try {
            metricsService.incrementGetClientsCounter();
            metricsService.recordGetClientsSummary(1);
            List<Client> clients = List.copyOf(ClientRepository.getAllClients());
            metricsService.recordGetClientsTimer(System.nanoTime() - startTime);
            return clients;
        } finally {
            meterRegistry.gauge("transaction_get_clients_active", metricsService.getGetClientsCount());
        }
    }

    @PostMapping("/select-recipient")
    @Operation(
            summary = "Выбор получателя",
            description = "Выбирает получателя и его счет для последующего перевода.",
            requestBody = @RequestBody(
                    description = "Данные получателя",
                    required = true,
                    content = @Content(
                            mediaType = "application/x-www-form-urlencoded",
                            schema = @Schema(implementation = Void.class),
                            examples = @ExampleObject(
                                    name = "Select Recipient Example",
                                    summary = "Пример выбора получателя",
                                    value = "username=user1&accountNumber=1234567890"
                            )
                    )
            )
    )
    public String selectRecipient(
            @RequestParam @Schema(description = "Имя пользователя получателя", example = "user1") String username,
            @RequestParam @Schema(description = "Номер счета получателя", example = "1234567890") String accountNumber
    ) {
        logger.debug("selectRecipient called with username: {}, accountNumber: {}", username, accountNumber);
        timeoutController.applyTimeout("select_recipient");
        long startTime = System.nanoTime();

        try {
            metricsService.incrementSelectRecipientCounter();
            metricsService.recordSelectRecipientSummary(username.length());
            if (!isAuthenticated()) {
                logger.warn("selectRecipient: User not authenticated");
                meterRegistry.counter("transaction_select_recipient_failure_total").increment();
                return "❌ Ошибка: Сначала войдите в систему!";
            }

            Optional<Client> recipientOpt = ClientRepository.findByUsername(username);
            if (recipientOpt.isEmpty()) {
                logger.warn("selectRecipient: Recipient not found for username: {}", username);
                meterRegistry.counter("transaction_select_recipient_failure_total").increment();
                return "❌ Ошибка: Получатель не найден!";
            }

            Optional<Account> recipientAccountOpt = recipientOpt.get().getAccounts().stream()
                    .filter(a -> a.getAccountNumber().equals(accountNumber))
                    .findFirst();

            if (recipientAccountOpt.isEmpty()) {
                logger.warn("selectRecipient: Account not found for accountNumber: {}", accountNumber);
                meterRegistry.counter("transaction_select_recipient_failure_total").increment();
                return "❌ Ошибка: У получателя нет такого счета!";
            }

            this.recipientClient = recipientOpt.get();
            this.recipientAccount = recipientAccountOpt.get();

            logger.info("selectRecipient: Recipient selected: {} (Account: {})", recipientClient.getFullName(), recipientAccount.getAccountNumber());
            metricsService.recordSelectRecipientTimer(System.nanoTime() - startTime);
            return "✅ Получатель выбран: " + recipientClient.getFullName() + " (Счет: " + recipientAccount.getAccountNumber() + ")";
        } finally {
            meterRegistry.gauge("transaction_select_recipient_active", metricsService.getSelectRecipientCount());
        }
    }

    @PostMapping("/transfer")
    @Operation(
            summary = "Перевод средств",
            description = "Выполняет перевод средств на счет получателя.",
            requestBody = @RequestBody(
                    description = "Сумма перевода",
                    required = true,
                    content = @Content(
                            mediaType = "application/x-www-form-urlencoded",
                            schema = @Schema(implementation = Void.class),
                            examples = @ExampleObject(
                                    name = "Transfer Example",
                                    summary = "Пример перевода",
                                    value = "amount=500.00"
                            )
                    )
            )
    )
    public String transfer(
            @RequestParam @Schema(description = "Сумма перевода", example = "500.00") double amount
    ) {
        logger.debug("transfer called with amount: {}", amount);
        timeoutController.applyTimeout("transfer");
        long startTime = System.nanoTime();

        try {
            metricsService.incrementTransferCounter();
            metricsService.recordTransferSummary(amount);
            if (!isAuthenticated()) {
                logger.warn("transfer: User not authenticated");
                meterRegistry.counter("transaction_transfer_failure_total").increment();
                return "❌ Ошибка: Сначала войдите в систему!";
            }

            if (recipientClient == null || recipientAccount == null) {
                logger.warn("transfer: Recipient not selected");
                meterRegistry.counter("transaction_transfer_failure_total").increment();
                return "❌ Ошибка: Сначала выберите получателя!";
            }

            Optional<Client> senderOpt = getLoggedInClient();
            if (senderOpt.isEmpty()) {
                logger.warn("transfer: Sender not found");
                meterRegistry.counter("transaction_transfer_failure_total").increment();
                return "❌ Ошибка: Не удалось определить отправителя!";
            }

            Client sender = senderOpt.get();
            Optional<Account> senderAccountOpt = sender.getAccounts().stream().findFirst();
            if (senderAccountOpt.isEmpty()) {
                logger.warn("transfer: Sender has no account");
                meterRegistry.counter("transaction_transfer_failure_total").increment();
                return "❌ Ошибка: У вас нет счета!";
            }

            Account senderAccount = senderAccountOpt.get();

            if (senderAccount.getBalance() < amount) {
                logger.warn("transfer: Insufficient balance for amount: {}", amount);
                meterRegistry.counter("transaction_transfer_failure_total").increment();
                return "❌ Ошибка: Недостаточно средств на счете!";
            }

            senderAccount.setBalance(senderAccount.getBalance() - amount);
            recipientAccount.setBalance(recipientAccount.getBalance() + amount);

            logger.info("transfer: Completed for amount: {} to account: {}", amount, recipientAccount.getAccountNumber());
            metricsService.recordTransferTimer(System.nanoTime() - startTime);
            meterRegistry.counter("transaction_transfer_success_total").increment();
            return "✅ Перевод завершен! " + amount + "₽ переведено на счет " + recipientAccount.getAccountNumber();
        } finally {
            meterRegistry.gauge("transaction_transfer_active", metricsService.getTransferCount());
        }
    }
}