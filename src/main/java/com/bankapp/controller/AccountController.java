package com.bankapp.controller;

import com.bankapp.model.Account;
import com.bankapp.service.AccountService;
import com.bankapp.service.CustomMetricsService;
import com.bankapp.controller.TimeoutController;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Счета", description = "Операции с банковскими счетами клиентов")
public class AccountController {

    private final AccountService accountService;
    private final CustomMetricsService metricsService;
    private final TimeoutController timeoutController;
    private final MeterRegistry meterRegistry;
    private final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    public AccountController(AccountService accountService, CustomMetricsService metricsService,
                             TimeoutController timeoutController, MeterRegistry meterRegistry) {
        this.accountService = accountService;
        this.metricsService = metricsService;
        this.timeoutController = timeoutController;
        this.meterRegistry = meterRegistry;
    }

    @PostMapping("/create")
    @Operation(
            summary = "Создание нового счета",
            description = "Создает новый банковский счет для клиента по предоставленному идентификатору клиента.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "ID клиента для создания счета",
                    required = true
            )
    )
    public Account create(
            @RequestParam
            @Parameter(description = "Идентификатор клиента для создания счета", example = "12345", required = true)
            String clientId
    ) {
        logger.info("Create account attempt for clientId: {}", clientId);
        timeoutController.applyTimeout("create_account");
        long startTime = System.nanoTime();

        try {
            metricsService.incrementCreateAccountCounter();
            metricsService.recordCreateAccountSummary(clientId.length());
            Account account = accountService.createAccount(clientId);
            metricsService.recordCreateAccountTimer(System.nanoTime() - startTime);
            return account;
        } finally {
            meterRegistry.gauge("account_create_active", metricsService.getCreateAccountCount());
        }
    }
}