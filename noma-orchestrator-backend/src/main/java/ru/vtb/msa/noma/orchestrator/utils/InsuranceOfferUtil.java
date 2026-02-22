package ru.vtb.msa.noma.orchestrator.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.vtb.msa.noma.orchestrator.integration.risk.enums.CoverageType;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
@Slf4j
public class InsuranceOfferUtil {

    /**
     * Определяет основной тип покрытия на основе строкового значения
     *
     * @param coverageTypeStr строковое представление типа покрытия
     * @return тип покрытия, или TERM по умолчанию
     */
    public static CoverageType determinePrimaryCoverageType(String coverageTypeStr) {
        if (coverageTypeStr == null || coverageTypeStr.isEmpty()) {
            return CoverageType.TERM;
        }
        try {
            return CoverageType.valueOf(coverageTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Неизвестный тип покрытия: {}, используется TERM по умолчанию", coverageTypeStr);
            return CoverageType.TERM;
        }
    }

    /**
     * Корректирует срок полиса, добавляя 5 лет
     *
     * @param policyTermYears исходный срок в годах (может быть null для WHOLE_LIFE)
     * @return скорректированный срок или null для пожизненного страхования
     */
    public static Integer adjustPolicyTerm(Integer policyTermYears) {
        if (policyTermYears == null) {
            return null;
        }
        return policyTermYears + 5;
    }

    /**
     * Генерирует уникальный номер полиса
     *
     * @return номер полиса в формате OFFER-XXXXXXXX
     */
    public static String generatePolicyNumber() {
        return "OFFER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Генерирует описание предложения на основе типа покрытия
     *
     * @param coverageType    тип покрытия
     * @param policyTermYears срок полиса в годах
     * @param riskFactor      коэффициент риска
     * @return описание предложения
     */
    public static String generateDescription(CoverageType coverageType, Integer policyTermYears, BigDecimal riskFactor) {
        return switch (coverageType) {
            case TERM -> String.format(
                    "Срочное страхование жизни на %d лет. Базовый тариф с учетом риска (коэффициент: %.2f).",
                    policyTermYears != null ? policyTermYears : 0,
                    riskFactor
            );
            case WHOLE_LIFE -> String.format(
                    "Пожизненное страхование. Покрытие действует всю жизнь. Коэффициент риска: %.2f.",
                    riskFactor
            );
            case INVESTMENT -> String.format(
                    "Инвестиционное страхование жизни. Комбинированное покрытие с инвестиционной составляющей. Коэффициент риска: %.2f.",
                    riskFactor
            );
        };
    }
}
