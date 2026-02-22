package ru.vtb.msa.noma.orchestrator.integration.risk.pojo;

import lombok.Builder;

@Builder
public record RiskResponse(
        Double riskFactor,
        String riskLevel
) {
}
