package ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientAddressInfo {
    private String city;
    private String index;
    private String street;
    private String number;
}
