package ru.vtb.msa.noma.orchestrator.integration.infoservice.sender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.vtb.msa.noma.orchestrator.integration.infoservice.sender.pojo.InfoRequestDto;
import ru.vtb.msa.noma.orchestrator.model.AccountUpdatedEventRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class InfoServiceSender {

    private final static String SYSTEM_MNEMO_CODE = "NOMA";
    private final static String SYSTEM_NUMBER = "3131";
    private final KafkaTemplate<String, InfoRequestDto> kafkaTemplate;

    public void send(AccountUpdatedEventRequest accountUpdatedEventRequest) {
        InfoRequestDto infoRequestDto = InfoRequestDto.builder()
                .accountId(accountUpdatedEventRequest.accountId())
                .balance(accountUpdatedEventRequest.balance())
                .status(accountUpdatedEventRequest.status())
                .timestamp(accountUpdatedEventRequest.timestamp())
                .systemMnemoCode(SYSTEM_MNEMO_CODE)
                .systemNumber(SYSTEM_NUMBER)
                .build();

        String topic = "noma_info_topic";

        try {
            kafkaTemplate.send(topic, infoRequestDto);
            log.info("Отправили сообщение в топик {},тело сообщения {}", topic,infoRequestDto);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
