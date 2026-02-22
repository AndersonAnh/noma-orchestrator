package ru.vtb.msa.noma.orchestrator.integration.complexcheck.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.vtb.msa.noma.orchestrator.config.complexcheck.ComplexCheckConfig;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.builder.ComplexCheckRequestBuilder;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckRequest;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckResponse;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountRequest;
import ru.vtb.msa.noma.orchestrator.model.InsuranceLifeRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class ComplexCheckClient {

    @Qualifier("complexCheckRestTemplate")
    private final RestTemplate restTemplate;

    private final ComplexCheckRequestBuilder complexCheckRequestBuilder;

    private final ComplexCheckConfig complexCheckConfig;

    private final RetryTemplate retryTemplate;


    public ComplexCheckResponse complexCheck(CreateAccountRequest request) {
        final ComplexCheckRequest checkRequest = complexCheckRequestBuilder.buildComplexCheckRequest(request);

        return retryTemplate.execute(context -> {
            log.info("Вызов комплексной проверки (попытка №{}): {}", context.getRetryCount() + 1, checkRequest);
            return restTemplate.postForObject(
                    complexCheckConfig.getComplexCheckUrl(),
                    checkRequest,
                    ComplexCheckResponse.class
            );
        });
    }

    public ComplexCheckResponse complexCheck(InsuranceLifeRequest request) {
        final ComplexCheckRequest checkRequest = complexCheckRequestBuilder.buildComplexCheckRequest(request);

        return retryTemplate.execute(context -> {
            log.info("Вызов комплексной проверки (попытка №{}): {}", context.getRetryCount() + 1, checkRequest);
            return restTemplate.postForObject(
                    complexCheckConfig.getComplexCheckUrl(),
                    checkRequest,
                    ComplexCheckResponse.class
            );
        });
    }
}