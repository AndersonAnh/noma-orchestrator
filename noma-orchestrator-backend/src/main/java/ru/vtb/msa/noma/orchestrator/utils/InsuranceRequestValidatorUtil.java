package ru.vtb.msa.noma.orchestrator.utils;

import lombok.experimental.UtilityClass;
import ru.vtb.msa.noma.orchestrator.exception.InsuranceRequestNullException;
import ru.vtb.msa.noma.orchestrator.exception.InvalidBaseInsuredAmountException;
import ru.vtb.msa.noma.orchestrator.exception.InvalidTermInMonthsException;
import ru.vtb.msa.noma.orchestrator.exception.UnsupportedInsurancePeriodException;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifeRequest;

import java.math.BigDecimal;
import java.util.Set;

@UtilityClass
public class InsuranceRequestValidatorUtil {

    private final Set<Integer> SUPPORTED_PERIODS = Set.of(6, 12, 60);

    public void validate(InsuranceLifeRequest request) {
        if (request == null) {
            throw new InsuranceRequestNullException();
        }
        if (request.insurancePeriod() == null || !SUPPORTED_PERIODS.contains(request.insurancePeriod())) {
            throw new UnsupportedInsurancePeriodException(request.insurancePeriod());
        }
        if (request.baseInsuredAmount() == null || request.baseInsuredAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidBaseInsuredAmountException();
        }
        if (request.termInMonths() == null || request.termInMonths() <= 0) {
            throw new InvalidTermInMonthsException();
        }
    }
}
