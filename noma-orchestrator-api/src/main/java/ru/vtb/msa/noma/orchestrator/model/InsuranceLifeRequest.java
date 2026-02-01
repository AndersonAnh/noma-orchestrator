package ru.vtb.msa.noma.orchestrator.model;

import lombok.Builder;
import ru.vtb.msa.noma.orchestrator.enums.CoverageType;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record InsuranceLifeRequest(
        String policyNumber,
        UUID clientId,
        UUID agentId,
        Integer insurancePeriod,
        Integer termInMonths,
        BigDecimal baseInsuredAmount,
        CoverageType coverageType,
        String specialConditions
) {
}
