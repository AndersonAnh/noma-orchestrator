package ru.vtb.msa.noma.orchestrator.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.vtb.msa.noma.orchestrator.enums.TransactionStatus;

import java.time.LocalDate;
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
    private LocalDate timestamp;

    @Version
    private Integer version;
}
