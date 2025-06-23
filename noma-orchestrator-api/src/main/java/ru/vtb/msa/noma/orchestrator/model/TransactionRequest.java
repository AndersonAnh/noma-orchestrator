package ru.vtb.msa.noma.orchestrator.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

@Schema(
        name = "TransactionRequest",
        description = "Запрос на создание или перевод транзакции"
)
public record TransactionRequest(

        @Schema(
                description = "UUID счёта-отправителя",
                example = "123e4567-e89b-12d3-a456-426614174000",
                required = true
        )
        @NotNull
        UUID senderAccountId,

        @Schema(
                description = "UUID счёта-получателя",
                example = "123e4567-e89b-12d3-a456-426614174001",
                required = true
        )
        @NotNull
        UUID receiverAccountId,

        @Schema(
                description = "Сумма перевода (должна быть положительной)",
                example = "150.50",
                required = true
        )
        @NotNull
        @Positive
        double amount,

        @Schema(
                description = "Валюта по стандарту ISO 4217",
                example = "USD",
                required = true
        )
        @NotNull
        String currency,

        @Schema(
                description = "Необязательный комментарий к транзакции",
                example = "Арендная плата за июнь"
        )
        String description
) {}
