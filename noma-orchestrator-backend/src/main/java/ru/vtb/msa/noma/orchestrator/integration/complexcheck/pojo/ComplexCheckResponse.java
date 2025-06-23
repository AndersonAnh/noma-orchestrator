package ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo;

import lombok.Builder;

@Builder
public record ComplexCheckResponse (
        ComplexCheckRequestResult requestResult
) {
}
