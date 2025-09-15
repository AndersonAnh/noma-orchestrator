package ru.vtb.msa.noma.orchestrator.integration.fraud.builder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.vtb.msa.noma.orchestrator.db.entity.Account;
import ru.vtb.msa.noma.orchestrator.integration.fraud.pojo.FraudRequest;
import ru.vtb.msa.noma.orchestrator.mapper.DtoMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FraudRequestBuilder {

    private final DtoMapper dtoMapper;

    /**
     * Преобразует двух JPA-сущностей Account в DTO и собирает FraudRequest.
     */
    public FraudRequest buildFraudRequest(Account accountSender, Account accountReceiver) {
        // конвертируем сущности в модельные DTO
        var senderDto   = dtoMapper.accountToDto(accountSender);
        var receiverDto = dtoMapper.accountToDto(accountReceiver);
        // собираем record
        return new FraudRequest(List.of(senderDto, receiverDto));
    }
}
