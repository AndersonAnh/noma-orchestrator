package ru.vtb.msa.noma.orchestrator.exception;

public class XRequestIdNotCorrectException extends RuntimeException {
    public XRequestIdNotCorrectException(String message) {
        super(message);
    }
}
