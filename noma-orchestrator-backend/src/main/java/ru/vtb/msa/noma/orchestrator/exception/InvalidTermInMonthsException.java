package ru.vtb.msa.noma.orchestrator.exception;

public class InvalidTermInMonthsException extends InsuranceValidationException {
    public InvalidTermInMonthsException() {
        super("termInMonths must be positive");
    }
}
