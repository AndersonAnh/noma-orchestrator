package ru.vtb.msa.noma.orchestrator.integration.fraud.pojo;

import lombok.Builder;
import ru.vtb.msa.noma.orchestrator.model.AccountDto;

import java.util.List;

@Builder
public record FraudRequest(List<AccountDto> accounts) {
}
