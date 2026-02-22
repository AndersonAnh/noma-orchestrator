package ru.vtb.msa.noma.orchestrator.config.riskassessment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("integration.risk")
public class RiskAssessmentConfig {
    private String riskUrl;
}
