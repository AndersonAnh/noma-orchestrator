package ru.vtb.msa.noma.orchestrator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vtb.msa.noma.orchestrator.exception.XRequestIdNotCorrectException;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountRequest;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountResponse;

@Service
@RequiredArgsConstructor
public class AccountService {
    public CreateAccountResponse createAccount (String xRequestId, CreateAccountRequest request) {
        validateHeader(xRequestId);
    }

    private void validateHeader (String xRequestId) {
        if (xRequestId == null || xRequestId.isBlank()){
            throw new XRequestIdNotCorrectException("Заголовок xRequestId обязателен");
        }
    }
}
