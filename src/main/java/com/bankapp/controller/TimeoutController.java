package com.bankapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/timeout")
@Tag(name = "Таймауты", description = "Управление таймаутами (задержками) для различных действий пользователей: перевод, выбор получателя, создание счёта и др.")
public class TimeoutController {

    private final Map<String, Integer> timeouts = new HashMap<>();

    public TimeoutController() {
        // Устанавливаем стандартные таймауты в секундах для действий
        timeouts.put("transfer", 1);
        timeouts.put("select-recipient", 1);
        timeouts.put("create", 1);
    }

    /**
     * Применяет задержку (таймаут) перед выполнением действия.
     * Используется контроллером аутентификации для симуляции задержки.
     */
    public void applyTimeout(String action) {
        int timeoutSeconds = timeouts.getOrDefault(action, 1);
        try {
            Thread.sleep(timeoutSeconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @GetMapping("/get")
    @Operation(
            summary = "Получить таймаут по действию",
            description = "Возвращает значение таймаута (в секундах), установленного для указанного действия.",
            parameters = {
                    @Parameter(
                            name = "action",
                            description = "Название действия (например, transfer, select-recipient, create)",
                            example = "transfer",
                            required = true
                    )
            }
    )
    public int getTimeout(@RequestParam String action) {
        return timeouts.getOrDefault(action, 10);
    }

    @PostMapping("/set")
    @Operation(
            summary = "Установить таймаут по действию",
            description = "Позволяет задать или изменить значение таймаута (в секундах) для конкретного действия.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Параметры запроса для установки таймаута",
                    required = true,
                    content = @Content(
                            mediaType = "application/x-www-form-urlencoded",
                            examples = @ExampleObject(
                                    name = "Set Timeout Example",
                                    summary = "Пример установки таймаута",
                                    value = "action=login&timeoutSeconds=5"
                            )
                    )
            )
    )
    public String setTimeout(
            @RequestParam
            @Schema(description = "Название действия (transfer, select-recipient, create и т.д.)", example = "transfer")
            String action,

            @RequestParam
            @Schema(description = "Значение таймаута в секундах", example = "5")
            int timeoutSeconds
    ) {
        timeouts.put(action, timeoutSeconds);
        return "Таймаут для '" + action + "' установлен на " + timeoutSeconds + " секунд.";
    }

    @GetMapping("/all")
    @Operation(
            summary = "Получить все текущие таймауты",
            description = "Возвращает карту всех действий и соответствующих им значений таймаутов (в секундах)."
    )
    public Map<String, Integer> getAllTimeouts() {
        return timeouts;
    }
}
