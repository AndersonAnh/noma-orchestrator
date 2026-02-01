package ru.vtb.msa.noma.orchestrator.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record TransferReceiptParams(UUID senderAccountId,
                                    UUID receiverAccountId,
                                    Double amount,
                                    String currency,
                                    String description,
                                    String timestamp,
                                    Double senderBalanceAfter,
                                    Double receiverBalanceAfter) {
}
