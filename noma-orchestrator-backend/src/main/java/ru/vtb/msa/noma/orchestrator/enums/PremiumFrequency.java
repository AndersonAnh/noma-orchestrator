package ru.vtb.msa.noma.orchestrator.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PremiumFrequency {
    MONTHLY("Ежемесячно"),
    QUARTERLY("Ежеквартально"),
    YEARLY("Ежегодно");

    private final String name;
}
