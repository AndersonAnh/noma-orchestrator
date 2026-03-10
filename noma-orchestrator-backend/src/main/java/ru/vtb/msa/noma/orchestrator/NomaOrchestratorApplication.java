package ru.vtb.msa.noma.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


@SpringBootApplication
@ConfigurationPropertiesScan("ru.vtb.msa.noma.orchestrator.config")
public class NomaOrchestratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(NomaOrchestratorApplication.class, args);
    }
}
