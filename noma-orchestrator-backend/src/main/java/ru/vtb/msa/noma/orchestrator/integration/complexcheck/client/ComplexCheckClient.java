package ru.vtb.msa.noma.orchestrator.integration.complexcheck.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.vtb.msa.noma.orchestrator.config.ComplexCheckConfig;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.builder.ComplexCheckRequestBuilder;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckRequest;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckResponse;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountRequest;

@Component
@RequiredArgsConstructor
public class ComplexCheckClient {

    @Qualifier("complexCheckRestTemplate")
    private final RestTemplate restTemplate;

    private final ComplexCheckRequestBuilder complexCheckRequestBuilder;

    private final ComplexCheckConfig complexCheckConfig;

    public ComplexCheckResponse complexCheck(CreateAccountRequest request) {

        ComplexCheckRequest checkRequest = complexCheckRequestBuilder.buildComplexCheckRequest(request);
        return restTemplate.postForObject(complexCheckConfig.getComplexCheckUrl(), checkRequest, ComplexCheckResponse.class);
    }
}
