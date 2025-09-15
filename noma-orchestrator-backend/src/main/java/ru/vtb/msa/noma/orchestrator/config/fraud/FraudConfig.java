package ru.vtb.msa.noma.orchestrator.config.fraud;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("integration.fraud")
public class FraudConfig {
    private String fraudUrl;
}
