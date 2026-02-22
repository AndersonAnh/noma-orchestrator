package ru.vtb.msa.noma.orchestrator.integration.infoservice.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.vtb.msa.InfoRequestDto;
import ru.vtb.msa.InfoSystemErrorMessage;
import ru.vtb.msa.noma.orchestrator.model.AccountUpdatedEventRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class InfoServiceSender {

    private final static String INFO_TOPIC = "noma_info_topic";
    private final static String INFO_ERROR_TOPIC = "info_system_error_topic";
    private final static String SYSTEM_MNEMO_CODE = "NOMA";
    private final static String SYSTEM_NUMBER = "31311";
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void send(AccountUpdatedEventRequest accountUpdatedEventRequest) {
        InfoRequestDto infoRequestDto = InfoRequestDto.builder()
                .accountId(accountUpdatedEventRequest.accountId())
                .balance(accountUpdatedEventRequest.balance())
                .status(accountUpdatedEventRequest.status())
                .timestamp(accountUpdatedEventRequest.timestamp())
                .systemMnemoCode(SYSTEM_MNEMO_CODE)
                .systemNumber(SYSTEM_NUMBER)
                .build();


        try {
            kafkaTemplate.send(INFO_TOPIC, infoRequestDto);
            log.info("Отправили сообщение в топик {},тело сообщения {}", INFO_TOPIC, infoRequestDto);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void sendErrorMessage(InfoSystemErrorMessage infoSystemErrorMessage) {
        try {
            kafkaTemplate.send(INFO_ERROR_TOPIC,infoSystemErrorMessage);
            log.info("Отправили сообщение в топик {},тело сообщения {}", INFO_TOPIC, infoSystemErrorMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
