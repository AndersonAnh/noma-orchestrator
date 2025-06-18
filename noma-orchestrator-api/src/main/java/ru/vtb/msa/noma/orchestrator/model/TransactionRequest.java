package ru.vtb.msa.noma.orchestrator.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record TransactionRequest(
        @NotNull UUID senderAccountId,
        @NotNull UUID receiverAccountId,
        @NotNull @Positive double amount,
        @NotNull String currency,
        String description
) {
}
