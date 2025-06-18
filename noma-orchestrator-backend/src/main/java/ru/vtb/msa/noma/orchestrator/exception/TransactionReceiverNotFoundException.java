package ru.vtb.msa.noma.orchestrator.exception;

public class TransactionReceiverNotFoundException extends RuntimeException {
    public TransactionReceiverNotFoundException(String message) {
        super(message);
    }
}
