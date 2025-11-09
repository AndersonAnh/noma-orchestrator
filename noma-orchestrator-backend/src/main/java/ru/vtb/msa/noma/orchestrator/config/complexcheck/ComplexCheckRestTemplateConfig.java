package ru.vtb.msa.noma.orchestrator.config.complexcheck;

import lombok.RequiredArgsConstructor;
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
    // Централизованная настройка таймаутов для RestTemplate
    @Bean("complexCheckRestTemplate")
    public RestTemplate complexCheckRestTemplate() {
        // Создаём RequestConfig для HttpClient 5.x
        org.apache.hc.client5.http.config.RequestConfig requestConfig =
                org.apache.hc.client5.http.config.RequestConfig.custom()
                        .setConnectTimeout(properties.getConnectTimeOut(), java.util.concurrent.TimeUnit.MILLISECONDS)
                        .setResponseTimeout(properties.getReadTimeOut(), java.util.concurrent.TimeUnit.MILLISECONDS)
                        .build();

        // HttpClient 5.x
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();

        // Фабрика для RestTemplate: Spring Boot 3.x требует именно класс org.apache.hc.client5.http.classic.HttpClient
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);
    }
}
