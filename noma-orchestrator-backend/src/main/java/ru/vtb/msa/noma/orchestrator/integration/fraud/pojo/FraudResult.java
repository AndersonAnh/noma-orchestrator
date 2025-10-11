package ru.vtb.msa.noma.orchestrator.integration.fraud.pojo;

import ru.vtb.msa.noma.orchestrator.integration.fraud.enums.FraudResponseStatus;

import java.util.UUID;

public record FraudResult(UUID accountId,
                          FraudResponseStatus fraudCheckStatus,
                          String comment) {
}
