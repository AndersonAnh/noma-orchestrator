package ru.vtb.msa.noma.orchestrator.integration.fraud.pojo;


import lombok.Builder;

import java.util.List;

@Builder
public record FraudResponse(List<FraudResult> results) {
}
