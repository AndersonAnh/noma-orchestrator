package ru.vtb.msa.noma.orchestrator.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    @NotNull
    //@JsonProperty("name")
    private String name;
    @NotNull
    private String taxId;
    @NotNull
    private String phone;
    private String email;
}
