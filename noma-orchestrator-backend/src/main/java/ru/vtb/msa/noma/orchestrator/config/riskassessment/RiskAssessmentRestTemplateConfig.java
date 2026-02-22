package ru.vtb.msa.noma.orchestrator.config.riskassessment;

import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import ru.vtb.msa.noma.orchestrator.config.retry.IntegrationRetryProperties;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class RiskAssessmentRestTemplateConfig {
    private final IntegrationRetryProperties properties;

    @Bean("riskRestTemplate")
    public RestTemplate riskRestTemplate() {
        RequestConfig requestConfig =
                RequestConfig.custom()
                        .setConnectTimeout(properties.getConnectTimeOut(), java.util.concurrent.TimeUnit.MILLISECONDS)
                        .setResponseTimeout(properties.getReadTimeOut(), java.util.concurrent.TimeUnit.MILLISECONDS)
                        .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(factory);
        
        // Явно устанавливаем JSON конвертер с поддержкой application/json
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        
        // Удаляем XML конвертер и оставля��м только JSON
        restTemplate.getMessageConverters().removeIf(converter -> converter.getClass().getName().contains("Xml"));
        restTemplate.getMessageConverters().add(0, jsonConverter);
        
        return restTemplate;
    }
}
