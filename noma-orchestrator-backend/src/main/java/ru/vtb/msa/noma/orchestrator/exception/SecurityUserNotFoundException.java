package ru.vtb.msa.noma.orchestrator.exception;

public class SecurityUserNotFoundException extends RuntimeException {
    public SecurityUserNotFoundException(String message) {
        super(message);
    }
}
