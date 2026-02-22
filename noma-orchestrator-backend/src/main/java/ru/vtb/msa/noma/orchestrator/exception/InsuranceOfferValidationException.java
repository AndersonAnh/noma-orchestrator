package ru.vtb.msa.noma.orchestrator.exception;

public class InsuranceOfferValidationException extends RuntimeException {
    public InsuranceOfferValidationException(String message) {
        super(message);
    }

    public InsuranceOfferValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
