package ru.vtb.msa.noma.orchestrator.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import ru.vtb.msa.noma.orchestrator.enums.CoverageType;
import ru.vtb.msa.noma.orchestrator.enums.PolicyStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "insurance_life_policy")
public class InsuranceLife {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(name = "policy_number", nullable = false, length = 50)
    private String policyNumber;

    @NotNull
    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @NotNull
    @Column(name = "agent_id", nullable = false)
    private UUID agentId;

    @NotNull
    @FutureOrPresent
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    @DecimalMax(value = "1000000000", inclusive = true)
    @Column(name = "insured_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal insuredAmount;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true)
    @Column(name = "premium_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal premiumAmount;

    @NotNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "policy_status", nullable = false, length = 20)
    private PolicyStatus policyStatus = PolicyStatus.DRAFT;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "coverage_type", nullable = false, length = 20)
    private CoverageType coverageType;

    @Size(max = 2000)
    @Column(name = "special_conditions", length = 2000)
    private String specialConditions;

    private Boolean isDeleted;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @NotBlank
    @Column(name = "created_by", nullable = false, updatable = false, length = 255)
    private String createdBy;

    @NotBlank
    @Column(name = "updated_by", nullable = false, length = 255)
    private String updatedBy;
}
