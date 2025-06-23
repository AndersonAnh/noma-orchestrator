package ru.vtb.msa.noma.orchestrator.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.UUID;

@Schema(
        description = "DTO для представления транзакции в ответе API",
        name = "TransactionDto"
)
public record TransactionDto(

        @Schema(
                description = "Уникальный идентификатор транзакции",
                example = "e7b9f8a0-3c2d-4f5e-9a6b-1234567890ab"
        )
        UUID id,

        @Schema(
                description = "UUID счёта-отправителя",
                example = "123e4567-e89b-12d3-a456-426614174000"
        )
        UUID senderAccountId,

        @Schema(
                description = "UUID счёта-получателя",
                example = "123e4567-e89b-12d3-a456-426614174001"
        )
        UUID receiverAccountId,

        @Schema(
                description = "Сумма транзакции",
                example = "250.75"
        )
        double amount,

        @Schema(
                description = "Валюта транзакции (ISO 4217)",
                example = "USD"
        )
        String currency,

        @Schema(
                description = "Описание или назначение платежа",
                example = "Арендная плата за июнь"
        )
        String description,

        @Schema(
                description = "Дата транзакции в формате yyyy-MM-dd",
                example = "2025-06-17"
        )
        LocalDate timestamp
) {}
