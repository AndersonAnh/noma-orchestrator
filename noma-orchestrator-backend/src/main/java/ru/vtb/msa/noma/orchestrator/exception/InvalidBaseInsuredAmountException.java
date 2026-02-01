package ru.vtb.msa.noma.orchestrator.exception;

public class InvalidBaseInsuredAmountException extends InsuranceValidationException {
    public InvalidBaseInsuredAmountException() {
        super("Base insured amount must be positive");
    }
}
