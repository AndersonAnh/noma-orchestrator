package ru.vtb.msa.noma.orchestrator.config.fraud;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FraudRestTemplateConfig {

    @Bean("fraudRestTemplate")
    public RestTemplate fraudRestTemplate() {
        return new RestTemplate();
    }
}
