package ru.vtb.msa.noma.orchestrator.model;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
public record AccountDto(
        UUID accountId,   // ← новое поле
        UserDto user,
        double  balance,
        String  currency,
        String  status,
        ZonedDateTime createdAt
) {}
