package ru.vtb.msa.noma.orchestrator.model;

import jakarta.validation.constraints.NotNull;

public record CreateAccountRequest(
        @NotNull UserDto user,
        @NotNull double balance,
        @NotNull String currency
) {
}
