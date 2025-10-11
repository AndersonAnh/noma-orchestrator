package ru.vtb.msa.noma.orchestrator.model;

import lombok.Builder;

import java.util.UUID;
@Builder
public record UserDto(
        String name,
        String taxId,
        String phone,
        String email
) {}
