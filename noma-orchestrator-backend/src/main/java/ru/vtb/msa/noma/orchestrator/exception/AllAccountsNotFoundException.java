package ru.vtb.msa.noma.orchestrator.exception;

public class AllAccountsNotFoundException extends RuntimeException {
    public AllAccountsNotFoundException(String message) {
        super(message);
    }
}
