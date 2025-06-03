package ru.vtb.msa.noma.orchestrator.controller;

import ru.vtb.msa.noma.orchestrator.AccountApi;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountRequest;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountResponse;

public class AccountApiController implements AccountApi {
    @Override
    public CreateAccountResponse createAccount(String xRequestId, CreateAccountRequest request) {
        return null;
    }
}
