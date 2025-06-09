package ru.vtb.msa.noma.orchestrator.model;

public record CreateAccountResponse(
        UserDto user,
        String status
) {
}
