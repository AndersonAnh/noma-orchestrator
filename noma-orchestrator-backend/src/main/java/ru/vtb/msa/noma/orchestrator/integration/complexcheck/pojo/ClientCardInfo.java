package ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientCardInfo {
    private String bank;
    private String number;
    private String salePoint;
    private String currency;
}
