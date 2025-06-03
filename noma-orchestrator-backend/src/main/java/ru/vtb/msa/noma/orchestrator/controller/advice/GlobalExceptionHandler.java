package ru.vtb.msa.noma.orchestrator.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.vtb.msa.noma.orchestrator.dto.ErrorDto;
import ru.vtb.msa.noma.orchestrator.exception.XRequestIdNotCorrectException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(XRequestIdNotCorrectException.class)
    protected ErrorDto handleIdentificationException(Exception exception) {
        log.warn(exception.getMessage(), exception);
        return ErrorDto.builder()
                .code("BAD_REQUEST")
                .header("Клиент не имеет прав на создание аккаунта.")
                .message("Клиент не имеет прав на создание аккаунта.")
                .build();
    }
}
