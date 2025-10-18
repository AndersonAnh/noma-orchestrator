package ru.vtb.msa.noma.orchestrator.config.retry;

import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RetryConfig {

    private final IntegrationRetryProperties properties;

    @Bean
    public RetryTemplate retryTemplate() throws ClassNotFoundException {
        RetryTemplate retryTemplate = new RetryTemplate();

        // Backoff policy
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(properties.getInitialInterval());
        backOffPolicy.setMultiplier(properties.getMultiplier());
        backOffPolicy.setMaxInterval(properties.getMaxInterval());
        retryTemplate.setBackOffPolicy(backOffPolicy);

        // Retryable exceptions policy
        Map<Class<? extends Throwable>, Boolean> retryableMap = new HashMap<>();
        if (properties.getRetryableExceptions() != null && !properties.getRetryableExceptions().isEmpty()) {
            for (String clazzName : properties.getRetryableExceptions()) {
                Class<?> clazz = Class.forName(clazzName);
                if (Throwable.class.isAssignableFrom(clazz)) {
                    retryableMap.put((Class<? extends Throwable>) clazz, true);
                }
            }
        } else {
            retryableMap = Collections.singletonMap(Exception.class, true);
        }
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(properties.getMaxAttempts(), retryableMap);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

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
