package ru.vtb.msa.noma.orchestrator.integration.infoservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.vtb.msa.OrchestratorInfoResponse;
import ru.vtb.msa.noma.orchestrator.integration.infoservice.service.InfoService;

@Component
@Slf4j
@RequiredArgsConstructor
public class InfoServiceListener {

    private final InfoService infoService;

    @KafkaListener(
            topics = "info_service_topic"
    )
    public void onInfoServiceEvent(OrchestratorInfoResponse orchestratorInfoResponse) {
        log.info("Получили событие: orchestratorInfoResponse={},", orchestratorInfoResponse);
        infoService.sendDuplicateMessage(orchestratorInfoResponse);
    }
}
