package ru.vtb.msa.noma.orchestrator.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PolicyStatus {
    DRAFT("Черновик"),
    ACTIVE("Активный"),
    SUSPENDED("Приостановлен"),
    EXPIRED("Истекший"),
    CANCELLED("Аннулирован");

    private final String description;
}
