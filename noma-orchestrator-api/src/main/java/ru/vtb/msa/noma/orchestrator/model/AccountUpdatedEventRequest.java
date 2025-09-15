package ru.vtb.msa.noma.orchestrator.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public record AccountUpdatedEventRequest(String accountId,
                                         double balance,
                                         String status,
                                         LocalDate timestamp
) {
}
