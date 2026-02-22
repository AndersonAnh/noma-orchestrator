package ru.vtb.msa.noma.orchestrator.service;

import ru.vtb.msa.noma.orchestrator.db.entity.InsuranceLife;
import ru.vtb.msa.noma.orchestrator.enums.CoverageType;
import ru.vtb.msa.noma.orchestrator.enums.PolicyStatus;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.enums.ComplexCheckResult;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckRequestResult;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckResponse;
import ru.vtb.msa.noma.orchestrator.integration.risk.pojo.RiskResponse;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifePolicyResponse;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifeRequest;
import ru.vtb.msa.noma.orchestrator.model.InsuranceOfferRequest;
import ru.vtb.msa.noma.orchestrator.model.InsuranceOfferResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
   public static InsuranceOfferRequest getValidOfferRequest() {
        return InsuranceOfferRequest.builder()
                .clientExternalId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .insuredAge(35)
                .desiredCoverageAmount(new BigDecimal("5000000.00"))
                .policyTermYears(10)
                .coverageType("WHOLE_LIFE")
                .isHighRiskOccupation(false)
                .hasDangerousHobbies(false)
                .beneficiaryRelation("Супруга")
                .build();
   }
    public static List<InsuranceOfferResponse> getInsuranceOfferResponses() {
        return List.of(
                new InsuranceOfferResponse(
                        "OFFER-WHOLE-LIFE-2024-001",
                        "WHOLE_LIFE",
                        new BigDecimal("5000000.00"),
                        new BigDecimal("45000.00"),
                        10,
                        "Пожизненное страхование жизни с гарантированной выплатой. Коэффициент риска: 1.0",
                        new BigDecimal("1.0")
                ),
                new InsuranceOfferResponse(
                        "OFFER-TERM-2024-001",
                        "TERM",
                        new BigDecimal("5000000.00"),
                        new BigDecimal("38500.00"),
                        10,
                        "Срочное страхование жизни на 10 лет. Более доступная премия. Коэффициент риска: 1.0",
                        new BigDecimal("1.0")
                ),
                new InsuranceOfferResponse(
                        "OFFER-INVESTMENT-2024-001",
                        "INVESTMENT",
                        new BigDecimal("4000000.00"),
                        new BigDecimal("52000.00"),
                        8,
                        "Инвестиционное страхование с возможностью роста капитала. Сумма 4 млн, срок 8 лет. Коэффициент риска: 1.0",
                        new BigDecimal("1.0")
                )
        );
    }

    public static RiskResponse getRiskResponse() {
        return RiskResponse.builder()
                .riskFactor(1.0)
                .riskLevel("LOW")
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
