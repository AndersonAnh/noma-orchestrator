package ru.vtb.msa.noma.orchestrator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.vtb.msa.noma.orchestrator.AccountApi;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountRequest;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountResponse;
import ru.vtb.msa.noma.orchestrator.service.AccountService;

@RestController
@RequiredArgsConstructor
public class AccountApiController implements AccountApi {

    private final AccountService accountService;

    @Override
    public CreateAccountResponse createAccount(String xRequestId, CreateAccountRequest request) {
        return accountService.createAccount(xRequestId, request);
    }
}
