package ru.vtb.msa.noma.orchestrator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vtb.msa.noma.orchestrator.db.entity.Account;
import ru.vtb.msa.noma.orchestrator.db.entity.User;
import ru.vtb.msa.noma.orchestrator.db.repositorty.AccountRepository;
import ru.vtb.msa.noma.orchestrator.db.repositorty.UserRepository;
import ru.vtb.msa.noma.orchestrator.enums.AccountStatus;
import ru.vtb.msa.noma.orchestrator.enums.Currency;
import ru.vtb.msa.noma.orchestrator.exception.ComplexCheckArbitrationException;
import ru.vtb.msa.noma.orchestrator.exception.ComplexCheckDenyException;
import ru.vtb.msa.noma.orchestrator.exception.XRequestIdNotCorrectException;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.client.ComplexCheckClient;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckResponse;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountRequest;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountResponse;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final ComplexCheckClient complexCheckClient;

    private final UserRepository userRepository;

    private final AccountRepository accountRepository;

    public CreateAccountResponse createAccount(String xRequestId, CreateAccountRequest request) {
        validateHeader(xRequestId);

        ComplexCheckResponse complexCheckResponse = complexCheckClient.complexCheck(request);
        complexCheckResponseProcessing(complexCheckResponse);

        User user = createNewUser(request);
        User savedUser = userRepository.save(user);

        Account account = createNewAccount(request, savedUser);
        Account savedAccount = accountRepository.save(account);

        return new CreateAccountResponse(request.user(), savedAccount.getStatus().name());
    }

    private void validateHeader(String xRequestId) {
        if (xRequestId == null || xRequestId.isBlank()) {
            throw new XRequestIdNotCorrectException("Заголовок xRequestId обязателен");
        }
    }

    private void complexCheckResponseProcessing(ComplexCheckResponse response) {
        var decision = response.requestResult().getDecision();
        switch (decision) {
            case DENY -> throw new ComplexCheckDenyException();
            case ARBITRATION -> throw new ComplexCheckArbitrationException();
        }
    }

    private Account createNewAccount(CreateAccountRequest request, User user) {
        return Account.builder()
                .user(user)
                .balance(request.balance())
                .currency(Currency.valueOf(request.currency()))
                .status(AccountStatus.ACTIVE)
                .createdAt(ZonedDateTime.now())
                .build();
    }

    private User createNewUser(CreateAccountRequest request) {
        return User.builder()
                .name(request.user().getName())
                .taxId(request.user().getTaxId())
                .phone(request.user().getPhone())
                .email(request.user().getEmail())
                .registrationDate(ZonedDateTime.now())
                .build();
    }
}
