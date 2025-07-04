package ru.vtb.msa.noma.orchestrator.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record TransactionResponse(
        @Schema(
                description = "Список транзакций"
        )
        List<TransactionDto> transactions
) {}