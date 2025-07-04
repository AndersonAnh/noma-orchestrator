package ru.vtb.msa.noma.orchestrator.exception;

public class TransactionSenderNotFoundException extends RuntimeException {
    public TransactionSenderNotFoundException(String message) {
        super(message);
    }
}
