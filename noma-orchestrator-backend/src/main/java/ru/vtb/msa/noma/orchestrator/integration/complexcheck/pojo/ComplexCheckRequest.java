package ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComplexCheckRequest {
    private ClientInfo client;
    private List<ClientCardInfo> cards;
    private ClientAddressInfo address;
    private String currency;
}
