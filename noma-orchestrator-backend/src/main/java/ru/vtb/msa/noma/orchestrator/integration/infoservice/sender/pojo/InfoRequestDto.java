package ru.vtb.msa.noma.orchestrator.integration.infoservice.sender.pojo;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record InfoRequestDto(
        String accountId,
        double balance,
        String status,
        LocalDate timestamp,
        String systemMnemoCode,
        String systemNumber
) {
}
