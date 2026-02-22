package ru.vtb.msa.noma.orchestrator.integration.risk.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.vtb.msa.noma.orchestrator.config.riskassessment.RiskAssessmentConfig;
import ru.vtb.msa.noma.orchestrator.integration.risk.builder.RiskRequestBuilder;
import ru.vtb.msa.noma.orchestrator.integration.risk.pojo.RiskRequest;
import ru.vtb.msa.noma.orchestrator.integration.risk.pojo.RiskResponse;
import ru.vtb.msa.noma.orchestrator.model.InsuranceOfferRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class RiskClient {

    @Qualifier("riskRestTemplate")
    private final RestTemplate restTemplate;

    private final RiskRequestBuilder riskRequestBuilder;

    private final RiskAssessmentConfig riskAssessmentConfig;

    private final RetryTemplate retryTemplate;

    public RiskResponse riskCheck(InsuranceOfferRequest request) {
        final RiskRequest riskRequest = riskRequestBuilder.buildRiskRequest(request);

        return retryTemplate.execute(context -> {
            log.info("Вызов сервиса Риска для оценки риска: {}", riskRequest);
            return restTemplate.postForObject(
                    riskAssessmentConfig.getRiskUrl(),
                    riskRequest,
                    RiskResponse.class
            );
        });
    }
}
