package ru.vtb.msa.noma.orchestrator.exception;

/**
 * Исключение, выбрасываемое когда клиент не найден в системе
 */
public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String message) {
        super(message);
    }

    public ClientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
