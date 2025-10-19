package ru.vtb.msa.noma.orchestrator.integration.fraud.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.vtb.msa.noma.orchestrator.config.fraud.FraudConfig;
import ru.vtb.msa.noma.orchestrator.db.entity.Account;
import ru.vtb.msa.noma.orchestrator.integration.fraud.builder.FraudRequestBuilder;
import ru.vtb.msa.noma.orchestrator.integration.fraud.pojo.FraudRequest;
import ru.vtb.msa.noma.orchestrator.integration.fraud.pojo.FraudResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class FraudClient {

    @Qualifier("fraudRestTemplate")
    private final RestTemplate restTemplate;

    private final FraudConfig fraudConfig;

    private final FraudRequestBuilder requestBuilder;

    private final RetryTemplate retryTemplate;

    /**
     * Отправляет в Fraud-сервис запрос по двум аккаунтам и возвращает полный FraudResponse.
     */
    public FraudResponse checkFraud(Account sender, Account receiver) {
        FraudRequest fraudRequest = requestBuilder.buildFraudRequest(sender, receiver);
        return retryTemplate.execute(context -> {
            log.info("Вызов проверки на мошенничество (попытка №{}): {}", context.getRetryCount() + 1, fraudRequest);
            return restTemplate.postForObject(
                    fraudConfig.getFraudUrl(),
                    fraudRequest,
                    FraudResponse.class
            );
        });
    }
}
