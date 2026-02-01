package ru.vtb.msa.noma.orchestrator.config.complexcheck;

import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ru.vtb.msa.noma.orchestrator.config.retry.IntegrationRetryProperties;

@Configuration
@RequiredArgsConstructor
public class ComplexCheckRestTemplateConfig {

    private final IntegrationRetryProperties properties;

    @Bean("complexCheckRestTemplate")
    public RestTemplate complexCheckRestTemplate() {
        RequestConfig requestConfig =
                RequestConfig.custom()
                        .setConnectTimeout(properties.getConnectTimeOut(), java.util.concurrent.TimeUnit.MILLISECONDS)
                        .setResponseTimeout(properties.getReadTimeOut(), java.util.concurrent.TimeUnit.MILLISECONDS)
                        .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);
    }
}
