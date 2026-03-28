package ru.vtb.msa.noma.orchestrator.security.dto;

public record AuthenticationRequest(
        String email,
        String password
) {
}
