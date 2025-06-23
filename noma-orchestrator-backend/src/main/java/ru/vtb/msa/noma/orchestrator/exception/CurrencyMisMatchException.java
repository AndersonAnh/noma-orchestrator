package ru.vtb.msa.noma.orchestrator.exception;

public class CurrencyMisMatchException extends RuntimeException {
    public CurrencyMisMatchException(String message) {
        super(message);
    }
}
