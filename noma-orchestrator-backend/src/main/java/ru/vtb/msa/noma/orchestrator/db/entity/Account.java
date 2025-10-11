package ru.vtb.msa.noma.orchestrator.db.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.vtb.msa.noma.orchestrator.enums.AccountStatus;
import ru.vtb.msa.noma.orchestrator.enums.Currency;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Double balance;
    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    @Version
    private Integer version;
}
