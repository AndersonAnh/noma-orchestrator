package ru.vtb.msa.noma.orchestrator.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record InsuranceOfferResponse(
        String policyNumber,
        String coverageType,
        BigDecimal coverageAmount,
        BigDecimal premiumAmount,
        Integer policyTermYears,
        String description,
        BigDecimal riskFactorFromService
) {
}
