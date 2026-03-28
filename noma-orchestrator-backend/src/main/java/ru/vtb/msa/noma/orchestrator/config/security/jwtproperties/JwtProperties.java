package ru.vtb.msa.noma.orchestrator.config.security.jwtproperties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "security.jwt")  // префикс из application.yml
public class JwtProperties {
    private String secret;      // соответствует ключу "secret"
    private Long expiration;    // соответствует ключу "expiration"
}