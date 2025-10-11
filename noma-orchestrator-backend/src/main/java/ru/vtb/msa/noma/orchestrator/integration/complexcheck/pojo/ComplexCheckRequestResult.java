package ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.enums.ComplexCheckResult;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComplexCheckRequestResult {
    ComplexCheckResult decision;
    String comment;
}
