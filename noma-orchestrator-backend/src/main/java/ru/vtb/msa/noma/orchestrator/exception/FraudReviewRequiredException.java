package ru.vtb.msa.noma.orchestrator.exception;

public class FraudReviewRequiredException extends RuntimeException {
    public FraudReviewRequiredException(String message) {
        super(message);
    }
}
