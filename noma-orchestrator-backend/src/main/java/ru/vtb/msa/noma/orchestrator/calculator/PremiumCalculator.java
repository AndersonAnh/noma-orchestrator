package ru.vtb.msa.noma.orchestrator.calculator;

import org.springframework.stereotype.Component;

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
}