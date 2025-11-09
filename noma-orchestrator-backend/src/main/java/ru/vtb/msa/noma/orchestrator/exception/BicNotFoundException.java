package ru.vtb.msa.noma.orchestrator.exception;

public class BicNotFoundException extends RuntimeException {
    public BicNotFoundException(String message) {
        super(message);
    }
}
