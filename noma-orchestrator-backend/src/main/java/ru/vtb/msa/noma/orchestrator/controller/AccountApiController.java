package ru.vtb.msa.noma.orchestrator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.vtb.msa.noma.orchestrator.AccountApi;
import ru.vtb.msa.noma.orchestrator.model.*;
import ru.vtb.msa.noma.orchestrator.service.AccountService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountApiController implements AccountApi {

    private final AccountService accountService;

    @Override
    public List<AccountDto> getAllAccounts(String authorizationHeader) {
        return accountService.getAllAccounts(authorizationHeader);
    }

    @Override
    public void deleteAccount(String authorizationHeader, String id) {
        accountService.deleteAccount(authorizationHeader, id);
    }

    @Override
    public UpdateAccountResponse updateAccount(String authorizationHeader, String id, UpdateAccountRequest updateAccountRequest) {
        return accountService.updateAccount(authorizationHeader,id,updateAccountRequest);
    }

    @Override
    public CreateAccountResponse createAccount(String xRequestId, CreateAccountRequest request) {
        return accountService.createAccount(xRequestId, request);
    }

    @Override
    public void getTransactionProcess(String xRequestId, TransactionRequest request) {
        accountService.getTransactionsProcess(xRequestId, request);
    }

    @Override
    public TransactionResponse getTransactionByDate(String xRequestId, LocalDate date) {
        return accountService.getTransactionByDate(xRequestId, date);
    }

    @Override
    public AccountDto getAccountById(String authorizationHeader, String uuid) {
        return accountService.getAccountById(authorizationHeader, uuid);
    }
}
