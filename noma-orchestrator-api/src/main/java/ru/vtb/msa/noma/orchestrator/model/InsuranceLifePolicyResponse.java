package ru.vtb.msa.noma.orchestrator.model;

import lombok.Builder;
import ru.vtb.msa.noma.orchestrator.enums.CoverageType;
import ru.vtb.msa.noma.orchestrator.enums.PolicyStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record InsuranceLifePolicyResponse(
        UUID id,
        String policyNumber,
        UUID clientId,
        UUID agentId,
        LocalDate startDate,
        LocalDate endDate,
        Integer termInMonths,
        BigDecimal baseInsuredAmount,
        BigDecimal insuredAmount,
        BigDecimal premiumAmount,
        PolicyStatus policyStatus,
        CoverageType coverageType,
        String specialConditions
) {
}