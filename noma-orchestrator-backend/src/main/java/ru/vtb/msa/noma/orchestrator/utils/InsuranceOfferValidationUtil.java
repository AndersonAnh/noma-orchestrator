package ru.vtb.msa.noma.orchestrator.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.vtb.msa.noma.orchestrator.exception.InsuranceOfferValidationException;
import ru.vtb.msa.noma.orchestrator.integration.risk.enums.CoverageType;
import ru.vtb.msa.noma.orchestrator.model.InsuranceOfferRequest;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
@Slf4j
public class InsuranceOfferValidationUtil {

    private static final int MIN_AGE = 18;
    private static final int MAX_AGE = 120;
    private static final BigDecimal MIN_COVERAGE = BigDecimal.valueOf(100_000);
    private static final BigDecimal MAX_COVERAGE = BigDecimal.valueOf(100_000_000);
    private static final int MIN_POLICY_TERM = 1;
    private static final int MAX_POLICY_TERM = 50;

    /**
     * Валидирует запрос на создание предложения страхования
     *
     * @param request запрос на создание предложения
     * @throws InsuranceOfferValidationException если валидация не пройдена
     */
    public static void validate(InsuranceOfferRequest request) {
        if (request == null) {
            log.warn("Запрос на создание предложения страхования равен null");
            throw new InsuranceOfferValidationException("Запрос не может быть пустым");
        }

        validateClientExternalId(request.clientExternalId());
        validateAge(request.insuredAge());
        validateCoverageAmount(request.desiredCoverageAmount());
        validatePolicyTerm(request.policyTermYears(), request.coverageType());
        validateCoverageType(request.coverageType());
    }

    /**
     * Валидирует ID клиента
     */
    private static void validateClientExternalId(UUID clientExternalId) {
        if (clientExternalId == null) {
            log.warn("ID клиента равен null");
            throw new InsuranceOfferValidationException("ID клиента не может быть пустым");
        }
    }

    /**
     * Валидирует возраст застрахованного лица
     */
    private static void validateAge(Integer age) {
        if (age == null) {
            log.warn("Возраст равен null");
            throw new InsuranceOfferValidationException("Возраст не может быть пустым");
        }

        if (age < MIN_AGE) {
            log.warn("Возраст меньше минимума: {}", age);
            throw new InsuranceOfferValidationException(
                    String.format("Возраст должен быть не менее %d лет", MIN_AGE)
            );
        }

        if (age > MAX_AGE) {
            log.warn("Возраст больше максимума: {}", age);
            throw new InsuranceOfferValidationException(
                    String.format("Возраст не должен превышать %d лет", MAX_AGE)
            );
        }
    }

    /**
     * Валидирует сумму покрытия
     */
    private static void validateCoverageAmount(BigDecimal coverageAmount) {
        if (coverageAmount == null) {
            log.warn("Сумма покрытия равна null");
            throw new InsuranceOfferValidationException("Сумма покрытия не может быть пустой");
        }

        if (coverageAmount.compareTo(MIN_COVERAGE) < 0) {
            log.warn("Сумма покрытия меньше минимума: {}", coverageAmount);
            throw new InsuranceOfferValidationException(
                    String.format("Минимальная сумма покрытия: %s руб", MIN_COVERAGE)
            );
        }

        if (coverageAmount.compareTo(MAX_COVERAGE) > 0) {
            log.warn("Сумма покрытия больше максимума: {}", coverageAmount);
            throw new InsuranceOfferValidationException(
                    String.format("Максимальная сумма покрытия: %s руб", MAX_COVERAGE)
            );
        }
    }

    /**
     * Валидирует срок полиса
     */
    private static void validatePolicyTerm(Integer policyTermYears, String coverageType) {
        // Для пожизненного страхования срок может быть null
        if ("WHOLE_LIFE".equalsIgnoreCase(coverageType)) {
            return;
        }

        if (policyTermYears == null) {
            log.warn("Срок полиса равен null для типа покрытия: {}", coverageType);
            throw new InsuranceOfferValidationException("Срок полиса не может быть пустым");
        }

        if (policyTermYears < MIN_POLICY_TERM) {
            log.warn("Срок полиса меньше минимума: {}", policyTermYears);
            throw new InsuranceOfferValidationException(
                    String.format("Минимальный срок полиса: %d год", MIN_POLICY_TERM)
            );
        }

        if (policyTermYears > MAX_POLICY_TERM) {
            log.warn("Срок полиса больше максимума: {}", policyTermYears);
            throw new InsuranceOfferValidationException(
                    String.format("Максимальный срок полиса: %d лет", MAX_POLICY_TERM)
            );
        }
    }

    /**
     * Валидирует тип покрытия
     */
    private static void validateCoverageType(String coverageType) {
        // Null или пустая строка - используется TERM по умолчанию
        if (coverageType == null || coverageType.isEmpty()) {
            return;
        }

        try {
            CoverageType.valueOf(coverageType.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Неизвестный тип покрытия: {}", coverageType);
            throw new InsuranceOfferValidationException(
                    String.format("Неподдерживаемый тип покрытия: %s. Допустимые значения: TERM, WHOLE_LIFE, INVESTMENT", coverageType)
            );
        }
    }
}
