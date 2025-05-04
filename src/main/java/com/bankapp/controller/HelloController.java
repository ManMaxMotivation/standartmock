package com.bankapp.controller;

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
@Tag(name = "Приветственное сообщение", description = "Метод для приветствия пользователя")
public class HelloController {

    private final CustomMetricsService metricsService;
    private final TimeoutController timeoutController;
    private final MeterRegistry meterRegistry;
    private final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    public HelloController(CustomMetricsService metricsService, TimeoutController timeoutController,
                           MeterRegistry meterRegistry) {
        this.metricsService = metricsService;
        this.timeoutController = timeoutController;
        this.meterRegistry = meterRegistry;
    }

    @GetMapping("/hello")
    @Operation(
            summary = "Приветствие пользователя",
            description = "Метод возвращает приветственное сообщение с именем пользователя. Если имя не указано, будет использовано значение по умолчанию: 'Гость'.",
            parameters = {
                    @Parameter(
                            name = "name",
                            description = "Имя пользователя для приветствия",
                            example = "Игорь",
                            required = false
                    )
            }
    )
    public String sayHello(
            @RequestParam(defaultValue = "Гость")
            @Parameter(description = "Имя пользователя для приветствия", example = "Игорь", required = false)
            String name
    ) {
        logger.info("Hello request for name: {}", name);
        timeoutController.applyTimeout("say_hello");
        long startTime = System.nanoTime();

        try {
            metricsService.incrementSayHelloCounter();
            metricsService.recordSayHelloSummary(name.length());
            String response = "Привет, " + name + "!";
            metricsService.recordSayHelloTimer(System.nanoTime() - startTime);
            return response;
        } finally {
            meterRegistry.gauge("hello_say_hello_active", metricsService.getSayHelloCount());
        }
    }
}