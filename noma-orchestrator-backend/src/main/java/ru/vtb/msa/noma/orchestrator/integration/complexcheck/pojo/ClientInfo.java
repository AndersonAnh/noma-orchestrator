package ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientInfo {

    private String fullName;
    private String taxId;
    private String phone;
    private String email;
}
