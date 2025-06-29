package ru.vtb.msa.noma.orchestrator.model;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record AccountDto(
        UserDto user,
        double balance,
        String currency,
        String status,
        ZonedDateTime createdAt
) {
}
