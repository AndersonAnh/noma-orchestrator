package ru.vtb.msa.noma.orchestrator.exception;

public class UnsupportedInsurancePeriodException extends InsuranceValidationException {
    public UnsupportedInsurancePeriodException(Integer period) {
        super("Неподдерживаемый срок страхования: " + period);
    }
}
