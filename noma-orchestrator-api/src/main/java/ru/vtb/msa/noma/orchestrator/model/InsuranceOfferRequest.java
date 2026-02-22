package ru.vtb.msa.noma.orchestrator.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record InsuranceOfferRequest(
        @NotNull UUID clientExternalId,
        @NotNull Integer insuredAge,
        @NotNull BigDecimal desiredCoverageAmount,
        Integer policyTermYears,
        String coverageType,
        Boolean isHighRiskOccupation,
        Boolean hasDangerousHobbies,
        String beneficiaryRelation
) {
}
