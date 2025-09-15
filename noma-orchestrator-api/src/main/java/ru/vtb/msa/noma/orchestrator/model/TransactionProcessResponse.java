package ru.vtb.msa.noma.orchestrator.model;

import ru.vtb.msa.noma.orchestrator.enums.TransactionProcessStatus;

public record TransactionProcessResponse(
        TransactionProcessStatus status,
        String comment
) {
}
