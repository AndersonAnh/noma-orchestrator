package ru.vtb.msa.noma.orchestrator.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDto {
    private String code;
    private String header;
    private String message;
}
