package ru.vtb.msa.noma.orchestrator.security.dto;

import lombok.Builder;


@Builder
public record RegistrationRequest(
        String firstName,
        String lastName,
        String email,
        String password
) {
}
