package ru.vtb.msa.noma.orchestrator.model;

import java.time.LocalDate;

public record AccountUpdatedEventRequest(String accountId,
                                         double balance,
                                         String status,
                                         LocalDate timestamp
) {
}
