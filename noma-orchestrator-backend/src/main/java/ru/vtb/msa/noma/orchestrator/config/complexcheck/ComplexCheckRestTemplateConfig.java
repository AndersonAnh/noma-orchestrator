package ru.vtb.msa.noma.orchestrator.config.complexcheck;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ComplexCheckRestTemplateConfig {

    @Bean("complexCheckRestTemplate")
    public RestTemplate complexCheckRestTemplate() {
        return new RestTemplate();
    }
}
