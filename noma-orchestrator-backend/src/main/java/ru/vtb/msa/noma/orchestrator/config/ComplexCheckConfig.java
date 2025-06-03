package ru.vtb.msa.noma.orchestrator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("integration.complex-check")
public class ComplexCheckConfig {

    private String complexCheckUrl;
}
