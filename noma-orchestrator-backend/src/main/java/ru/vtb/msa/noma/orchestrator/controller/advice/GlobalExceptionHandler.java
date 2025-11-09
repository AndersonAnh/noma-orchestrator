package ru.vtb.msa.noma.orchestrator.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.ResourceAccessException;
import ru.vtb.msa.noma.orchestrator.dto.ErrorDto;
import ru.vtb.msa.noma.orchestrator.exception.*;

import java.io.IOException;

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

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ComplexCheckDenyException.class)
    protected ErrorDto handleComplexCheckProcessingDenyException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return ErrorDto.builder()
                .code("CONFLICT")
                .header("Ошибка при выполнении операции.")
                .message("Произошла внутренняя ошибка при выполнении операции. Создайте обращение в службу поддержки")
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.PROCESSING)
    @ExceptionHandler(ComplexCheckArbitrationException.class)
    protected ErrorDto handleComplexCheckProcessingArbitrationException(Exception exception) {
        log.warn(exception.getMessage(), exception);
        return ErrorDto.builder()
                .code("PROCESSING")
                .header("Ошибка при выполнении операции.")
                .message("Произошла внутренняя ошибка при выполнении операции. Создайте обращение в службу поддержки")
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({TransactionSenderNotFoundException.class, TransactionReceiverNotFoundException.class,
            AccountNotFoundException.class})
    protected ErrorDto handleCheckIdAccount(Exception exception) {
        log.warn(exception.getMessage(), exception);
        return ErrorDto.builder()
                .code("404")
                .header("Произошла внутренняя ошибка при выполнении операции. Создайте обращение в службу поддержки")
                .message("Произошла внутренняя ошибка при выполнении операции. Создайте обращение в службу поддержки")
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotEnoughFundsException.class)
    protected ErrorDto handleNotEnoughFundsException(Exception exception) {
        log.warn(exception.getMessage(), exception);
        return ErrorDto.builder()
                .code("400")
                .header("Произошла внутренняя ошибка при выполнении операции. Создайте обращение в службу поддержки")
                .message("Недостаточно средств")
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(FraudDetectedException.class)
    protected ErrorDto handleFraudDetectedException(FraudDetectedException exception) {
        log.error(exception.getMessage(), exception);
        return ErrorDto.builder()
                .code("CONFLICT")
                .header("Операция отклонена: мошенничество подтверждено.")
                .message("Транзакция заблокирована из-за подтверждённого мошенничества.")
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.PROCESSING)
    @ExceptionHandler(FraudReviewRequiredException.class)
    protected ErrorDto handleFraudReviewRequiredException(FraudReviewRequiredException exception) {
        log.warn(exception.getMessage(), exception);
        return ErrorDto.builder()
                .code("PROCESSING")
                .header("Подозрение на мошенничество.")
                .message("Требуется ручная проверка операции.")
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(FraudServiceException.class)
    protected ErrorDto handleFraudServiceException(FraudServiceException exception) {
        log.error(exception.getMessage(), exception);
        return ErrorDto.builder()
                .code("ERROR")
                .header("Ошибка проверки мошенничества.")
                .message("Не удалось выполнить проверку мошенничества. Попробуйте позже.")
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler({
            java.net.SocketTimeoutException.class,
            ResourceAccessException.class,
            IOException.class
    })
    protected ErrorDto handleIntegrationException(Exception exception) {
        log.error("Ошибка интеграции с внешним сервисом: " + exception.getMessage(), exception);
        return ErrorDto.builder()
                .code("INTEGRATION_UNAVAILABLE")
                .header("Внешний сервис временно недоступен")
                .message("Операция не выполнена из-за временной недоступности внешнего сервиса. Пожалуйста, попробуйте позже или обратитесь в службу поддержки.")
                .build();
    }
}


