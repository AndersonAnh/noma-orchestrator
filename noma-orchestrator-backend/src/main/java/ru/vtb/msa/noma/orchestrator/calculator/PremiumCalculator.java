package ru.vtb.msa.noma.orchestrator.calculator;

import org.springframework.stereotype.Component;
import ru.vtb.msa.noma.orchestrator.integration.risk.enums.CoverageType;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PremiumCalculator {

    /**
     * Расчет премии по формуле из ТЗ:
     * - 6 месяцев: коэффициент 1.5
     * - 12 месяцев: коэффициент 2.5
     * - 60 месяцев: коэффициент 4.0
     */
    public BigDecimal calculatePremium(BigDecimal baseInsuredAmount, Integer insurancePeriod) {
        BigDecimal coefficient = switch (insurancePeriod) {
            case 6 -> BigDecimal.valueOf(1.5);
            case 12 -> BigDecimal.valueOf(2.5);
            case 60 -> BigDecimal.valueOf(4.0);
            default -> throw new IllegalArgumentException(
                    "Неподдерживаемый срок страхования: " + insurancePeriod + " месяцев"
            );
        };

        BigDecimal premium = baseInsuredAmount.multiply(coefficient);

        return premium.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateInsuredAmount(BigDecimal baseAmount, Integer insurancePeriod) {
        return switch (insurancePeriod) {
            case 6 -> baseAmount;
            case 12 -> baseAmount.multiply(new BigDecimal("1.2"));
            case 60 -> baseAmount.multiply(new BigDecimal("2.0"));
            default -> baseAmount;
        };
    }

    /**
     * Расчет премии по формуле:
     * premiumAmount = (baseRate * desiredCoverageAmount * policyTermYears * riskFactor * coverageTypeMultiplier) / 1000
     *
     * @param baseRate базовый коэффициент (зависит от типа покрытия)
     * @param desiredCoverageAmount желаемая сумма покрытия
     * @param policyTermYears срок полиса в годах (может быть null для WHOLE_LIFE)
     * @param riskFactor коэффициент риска от сервиса
     * @param coverageTypeMultiplier множитель типа покры��ия
     * @return рассчитанная премия, округленная до 2 знаков
     */
    public BigDecimal calculateInsurancePremium(
            BigDecimal baseRate,
            BigDecimal desiredCoverageAmount,
            Integer policyTermYears,
            BigDecimal riskFactor,
            BigDecimal coverageTypeMultiplier) {

        BigDecimal termYears = policyTermYears != null ? BigDecimal.valueOf(policyTermYears) : BigDecimal.ONE;

        BigDecimal premium = baseRate
                .multiply(desiredCoverageAmount)
                .multiply(termYears)
                .multiply(riskFactor)
                .multiply(coverageTypeMultiplier)
                .divide(BigDecimal.valueOf(1000), 10, RoundingMode.HALF_UP);

        return premium.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Расчет премии с использованием типа покрытия
     */
    public BigDecimal calculateInsurancePremium(
            BigDecimal desiredCoverageAmount,
            Integer policyTermYears,
            BigDecimal riskFactor,
            CoverageType coverageType) {

        return calculateInsurancePremium(
                coverageType.getBaseRate(),
                desiredCoverageAmount,
                policyTermYears,
                riskFactor,
                coverageType.getMultiplier()
        );
    }
}