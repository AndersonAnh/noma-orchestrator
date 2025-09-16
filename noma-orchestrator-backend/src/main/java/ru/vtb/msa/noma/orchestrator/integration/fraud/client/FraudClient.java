package ru.vtb.msa.noma.orchestrator.integration.fraud.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.vtb.msa.noma.orchestrator.config.fraud.FraudConfig;
import ru.vtb.msa.noma.orchestrator.db.entity.Account;
import ru.vtb.msa.noma.orchestrator.integration.fraud.builder.FraudRequestBuilder;
import ru.vtb.msa.noma.orchestrator.integration.fraud.pojo.FraudRequest;
import ru.vtb.msa.noma.orchestrator.integration.fraud.pojo.FraudResponse;
import ru.vtb.msa.noma.orchestrator.utils.JsonUtil;

@Component
@RequiredArgsConstructor
@Slf4j
public class FraudClient {

    @Qualifier("fraudRestTemplate")
    private final RestTemplate restTemplate;

    private final FraudConfig fraudConfig;

    private final FraudRequestBuilder requestBuilder;

    /**
     * Отправляет в Fraud-сервис запрос по двум аккаунтам и возвращает полный FraudResponse.
     */
    public FraudResponse checkFraud(Account sender, Account receiver) {
        FraudRequest fraudRequest = requestBuilder.buildFraudRequest(sender, receiver);
        log.debug("Запрос в сервис по проверке на мошенничество {}", JsonUtil.toJson(fraudRequest));
        return restTemplate.postForObject(
                fraudConfig.getFraudUrl(),
                fraudRequest,
                FraudResponse.class
        );
    }
}
