package ru.vtb.msa.noma.orchestrator.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.UUID;

@Schema(
        name = "TransactionRequest",
        description = "Запрос на создание или перевод транзакции"
)
@Builder
public record TransactionRequest(

        @Schema(
                description = "UUID счёта-отправителя",
                example = "123e4567-e89b-12d3-a456-426614174000"
        )
        @NotNull
        UUID senderAccountId,

        @Schema(
                description = "UUID счёта-получателя",
                example = "123e4567-e89b-12d3-a456-426614174001"
        )
        @NotNull
        UUID receiverAccountId,

        @Schema(
                description = "Сумма перевода (должна быть положительной)",
                example = "150.50"
        )
        @NotNull
        @Positive
        double amount,

        @Schema(
                description = "Валюта по стандарту ISO 4217",
                example = "USD"
        )
        @NotNull
        String currency,

        @Schema(
                description = "Необязательный комментарий к транзакции",
                example = "Арендная плата за июнь"
        )
        String description
) {}