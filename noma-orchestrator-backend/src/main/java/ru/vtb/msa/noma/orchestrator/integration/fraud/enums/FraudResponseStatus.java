package ru.vtb.msa.noma.orchestrator.integration.fraud.enums;

public enum FraudResponseStatus {
    /**
     * Транзакция признана безопасной, мошенничество не выявлено.
     */
    SAFE,

    /**
     * Подозрение на мошенничество, требуется ручная проверка (escalation).
     */
    REVIEW_REQUIRED,

    /**
     * Транзакция отклонена из-за явного мошенничества.
     */
    FRAUD_CONFIRMED,

    /**
     * Сервис ещё не завершил проверку (например, асинхронно).
     */
    PENDING,

    /**
     * Проверка невозможна из-за технической ошибки.
     */
    ERROR
}
