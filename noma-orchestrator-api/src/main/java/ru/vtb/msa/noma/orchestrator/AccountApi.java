package ru.vtb.msa.noma.orchestrator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountRequest;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountResponse;
import ru.vtb.msa.noma.orchestrator.model.TransactionRequest;

public interface AccountApi {

    @PostMapping("api/account/create/")
    @Operation(description = "Метод отвечает за создание аккаунта клиента")
    CreateAccountResponse createAccount(
            @Parameter(description = " UUID для авторизации внутри системы")
            @RequestHeader(MsaAdditionalHeaders.X_REQUEST_ID)
            String xRequestId,
            @Parameter(description = "Тело запроса")
            @RequestBody CreateAccountRequest request
    );

    @PostMapping("api/account/transactions/")
    @Operation(description = "Метод реализовывает процесс перевода средств между счетами клиентов с резервированием средств")
     void getTransactionProcess(
            @Parameter(description = " UUID для авторизации внутри системы")
            @RequestHeader(MsaAdditionalHeaders.X_REQUEST_ID)
            String xRequestId,
            @Parameter(description = "Тело запроса")
            @RequestBody TransactionRequest request
            );
}
