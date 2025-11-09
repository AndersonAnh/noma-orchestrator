package ru.vtb.msa.noma.orchestrator.dto;

import java.util.UUID;

public record TransferReceiptParams(UUID senderAccountId,
                                    UUID receiverAccountId,
                                    Double amount,
                                    String currency,
                                    String description,
                                    String timestamp,
                                    Double senderBalanceAfter,
                                    Double receiverBalanceAfter) {
}
