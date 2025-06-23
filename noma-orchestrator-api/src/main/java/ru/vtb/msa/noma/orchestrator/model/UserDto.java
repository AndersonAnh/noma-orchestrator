package ru.vtb.msa.noma.orchestrator.model;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Schema(description = "User data transfer object")
@Builder
@Getter
@Setter
public class UserDto {
        @NotNull
        @Schema(description = "Имя пользователя", example = "Ivan Ivanov")
        String name;

        @NotNull
        @Schema(description = "Идентификатор налогообложения (ИНН)", example = "7707083893")
        String taxId;

        @NotNull
        @Schema(description = "Телефон пользователя", example = "+71234567890")
        String phone;

        @Schema(description = "Email пользователя", example = "ivan@example.com")
        String email;
}
