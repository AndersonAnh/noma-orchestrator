package ru.vtb.msa.noma.orchestrator.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MetricPostfix {
    SUCCESS("success"),
    ERROR("error");

    private final String postfix;
}