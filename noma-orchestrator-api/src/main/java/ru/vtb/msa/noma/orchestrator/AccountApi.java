package ru.vtb.msa.noma.orchestrator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountRequest;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountResponse;
import ru.vtb.msa.noma.orchestrator.model.TransactionRequest;
import ru.vtb.msa.noma.orchestrator.model.TransactionResponse;

import java.time.LocalDate;

@RequestMapping("api/account")
public interface AccountApi {

    @PostMapping("/create")
    @Operation(description = "Метод отвечает за создание аккаунта клиента")
    CreateAccountResponse createAccount(
            @Parameter(description = " UUID для авторизации внутри системы")
            @RequestHeader(MsaAdditionalHeaders.X_REQUEST_ID)
            String xRequestId,
            @Parameter(description = "Тело запроса")
            @RequestBody CreateAccountRequest request
    );

    @PostMapping("/transactions")
    @Operation(description = "Метод реализовывает процесс перевода средств между счетами клиентов с резервированием средств")
    void getTransactionProcess(
            @Parameter(description = "UUID для авторизации внутри системы")
            @RequestHeader(MsaAdditionalHeaders.X_REQUEST_ID)
            String xRequestId,
            @Parameter(description = "Тело запроса")
            @RequestBody TransactionRequest request
    );

    @GetMapping("getTransactionsByDate/{date}")
    @Operation(description = "Метод отвечает за получение транзакций по дате")
    TransactionResponse getTransactionByDate(
            @Parameter(
                    description = "UUID для авторизации внутри системы",
                    required = true,
                    in = ParameterIn.HEADER,
                    name = MsaAdditionalHeaders.X_REQUEST_ID
            )
            @RequestHeader(MsaAdditionalHeaders.X_REQUEST_ID)
            String xRequestId,

            @Parameter(
                    description = "Дата в формате yyyy-MM-dd, по которой фильтруются транзакции",
                    required = true,
                    in = ParameterIn.PATH,
                    name = "date",
                    schema = @Schema(type = "string", format = "date", example = "2025-06-17")
            )
            @PathVariable("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    );
}