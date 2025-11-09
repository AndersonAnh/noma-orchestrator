package ru.vtb.msa.noma.orchestrator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.vtb.msa.noma.orchestrator.AccountApi;
import ru.vtb.msa.noma.orchestrator.MsaAdditionalHeaders;
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
        return accountService.updateAccount(authorizationHeader, id, updateAccountRequest);
    }

    @Override
    public void accountUpdatedEvent(AccountUpdatedEventRequest account) {
        accountService.accountUpdatedEvent(account);
    }

    @Override
    public CreateAccountResponse createAccount(String xRequestId, CreateAccountRequest request) {
        return accountService.createAccount(xRequestId, request);
    }

    @Override
    public ResponseEntity<byte[]> transactionProcess(
            @RequestHeader(MsaAdditionalHeaders.X_REQUEST_ID) String xRequestId,
            @RequestBody TransactionRequest request
    ) {
        byte[] pdf = accountService.transactionsProcess(xRequestId, request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"transfer-receipt.pdf\"");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }


    @Override
    public TransactionResponse getTransactionByDate(String xRequestId, LocalDate date) {
        return accountService.getTransactionByDate(xRequestId, date);
    }

    @Override
    public AccountDto getAccountById(String authorizationHeader, String uuid) {
        return accountService.getAccountById(authorizationHeader, uuid);
    }

    @Override
    public BanksAndTypesResponseDto getBanksAndTypes() {
        return accountService.getBanksAndTypes();
    }
}
