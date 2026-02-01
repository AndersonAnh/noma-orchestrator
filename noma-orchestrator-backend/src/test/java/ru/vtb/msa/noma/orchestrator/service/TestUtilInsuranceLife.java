package ru.vtb.msa.noma.orchestrator.service;

import ru.vtb.msa.noma.orchestrator.db.entity.InsuranceLife;
import ru.vtb.msa.noma.orchestrator.enums.CoverageType;
import ru.vtb.msa.noma.orchestrator.enums.PolicyStatus;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.enums.ComplexCheckResult;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckRequestResult;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckResponse;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifePolicyResponse;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifeRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class TestUtilInsuranceLife {

    public static InsuranceLifeRequest getValidRequest() {
        return InsuranceLifeRequest.builder()
                .policyNumber("1234567890")
                .clientId(UUID.randomUUID())
                .agentId(UUID.randomUUID())
                .insurancePeriod(6)
                .termInMonths(12)
                .baseInsuredAmount(BigDecimal.valueOf(10000))
                .coverageType(CoverageType.FULL_LIFE)
                .specialConditions("Специальные условия")
                .build();
    }

    public static ComplexCheckResponse createAllowResponse() {
        ComplexCheckRequestResult requestResult = new ComplexCheckRequestResult();
        requestResult.setDecision(ComplexCheckResult.ALLOW);
        requestResult.setComment("Allowed");
        return ComplexCheckResponse.builder()
                .requestResult(requestResult)
                .build();
    }

    public static InsuranceLife createInsurance() {
        return InsuranceLife.builder()
                .id(UUID.randomUUID())
                .policyNumber("POL-" + System.currentTimeMillis())
                .clientId(UUID.randomUUID())
                .agentId(UUID.randomUUID())
                .startDate(LocalDate.now().plusDays(7))
                .endDate(LocalDate.now().plusYears(1))
                .insuredAmount(BigDecimal.valueOf(1000000))
                .premiumAmount(BigDecimal.valueOf(12000))
                .policyStatus(PolicyStatus.DRAFT)
                .coverageType(CoverageType.FULL_LIFE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy("test_user")
                .updatedBy("test_user")
                .build();
    }

    public static InsuranceLifePolicyResponse createFullResponse() {
        UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID clientId = UUID.fromString("660e8400-e29b-41d4-a716-446655440001");
        UUID agentId = UUID.fromString("770e8400-e29b-41d4-a716-446655440002");
        LocalDate startDate = LocalDate.now().plusDays(7);
        LocalDate endDate = startDate.plusYears(2);
        int termInMonths = calculateTermInMonths(startDate, endDate);

        return InsuranceLifePolicyResponse.builder()
                .id(id)
                .policyNumber("LIFE-POL-2024-001")
                .clientId(clientId)
                .agentId(agentId)
                .startDate(startDate)
                .endDate(endDate)
                .termInMonths(termInMonths)
                .baseInsuredAmount(new BigDecimal("2000000.00"))
                .insuredAmount(new BigDecimal("2200000.00"))
                .premiumAmount(new BigDecimal("24000.00"))
                .policyStatus(PolicyStatus.ACTIVE)
                .coverageType(CoverageType.FULL_LIFE)
                .specialConditions("""
                        Особые условия страхования:
                        1. Страхование от несчастных случаев
                        2. Критические заболевания
                        3. Инвалидность
                        4. Льготный период оплаты
                        """)
                .build();
    }

    private static int calculateTermInMonths(LocalDate startDate, LocalDate endDate) {
        return (int) java.time.temporal.ChronoUnit.MONTHS.between(startDate, endDate);
    }
}
