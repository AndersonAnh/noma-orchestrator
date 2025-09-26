package ru.vtb.msa.noma.orchestrator.integration.infoservice.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.vtb.msa.OrchestratorInfoResponse;

@Component
@Slf4j
public class InfoServiceListener {

    @KafkaListener(
            topics = "info_service_topic"
    )
    public void onInfoServiceEvent(OrchestratorInfoResponse orchestratorInfoResponse) {
        log.info("Получили событие: orchestratorInfoResponse={},", orchestratorInfoResponse);
    }
}
