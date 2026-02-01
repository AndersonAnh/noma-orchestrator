package ru.vtb.msa.noma.orchestrator.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CoverageType {
    FULL_LIFE("Пожизненное страхование"),
    TERM_LIFE("Срочное страхование"),
    ENDOWMENT("Накопительное страхование");

    private String description;
}
