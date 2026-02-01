package ru.vtb.msa.noma.orchestrator.exception;

public class InsuranceRequestNullException extends InsuranceValidationException {
    public InsuranceRequestNullException() {
        super("Request must not be null");
    }
}
