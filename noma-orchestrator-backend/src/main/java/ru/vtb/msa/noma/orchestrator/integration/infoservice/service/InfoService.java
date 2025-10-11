package ru.vtb.msa.noma.orchestrator.integration.infoservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.vtb.msa.InfoSystemErrorMessage;
import ru.vtb.msa.OrchestratorInfoResponse;
import ru.vtb.msa.noma.orchestrator.integration.infoservice.processor.InfoSystemProcessor;
import ru.vtb.msa.noma.orchestrator.integration.infoservice.sender.InfoServiceSender;

@Service
@Slf4j
@RequiredArgsConstructor
public class InfoService {

    private final static String DUPLICATE_ERROR_COMMENT = "Дублирующее сообщение: уже получено ранее за последние 24 часа";
    private final InfoSystemProcessor infoSystemProcessor;
    private final InfoServiceSender infoServiceSender;

    public void sendDuplicateMessage(OrchestratorInfoResponse orchestratorInfoResponse) {
        boolean first = infoSystemProcessor.firstTime(orchestratorInfoResponse);

        if (first) {
            log.info("Сообщение обработано впервые: accountId={}, timestamp={}",
                    orchestratorInfoResponse.accountId(), orchestratorInfoResponse.timestamp());
        } else {
            InfoSystemErrorMessage errorMessage = new InfoSystemErrorMessage(
                    orchestratorInfoResponse.accountId(),
                    orchestratorInfoResponse.timestamp(),
                    DUPLICATE_ERROR_COMMENT
            );

            infoServiceSender.sendErrorMessage(errorMessage);
        }
    }
}
