package ru.vtb.msa.noma.orchestrator.db.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.vtb.msa.noma.orchestrator.enums.TransactionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue
    private UUID id;
    private UUID senderAccountId;
    private UUID receiverAccountId;
    private Double amount;
    private String currency;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransactionStatus status;
    private String description;
    private LocalDateTime timestamp;

    @Version
    private Integer version;
}
