package ru.vtb.msa.noma.orchestrator.integration.risk.enums;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum CoverageType {
    TERM(new BigDecimal("0.001"), new BigDecimal("1.0")),
    WHOLE_LIFE(new BigDecimal("0.002"), new BigDecimal("1.5")),
    INVESTMENT(new BigDecimal("0.002"), new BigDecimal("2.0"));

    private final BigDecimal baseRate;
    private final BigDecimal multiplier;

    CoverageType(BigDecimal baseRate, BigDecimal multiplier) {
        this.baseRate = baseRate;
        this.multiplier = multiplier;
    }

    public CoverageType getAlternativeCoverageType() {
        return switch (this) {
            case TERM -> WHOLE_LIFE;
            case WHOLE_LIFE -> INVESTMENT;
            case INVESTMENT -> TERM;
        };
    }
}
