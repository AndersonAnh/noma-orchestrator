package ru.vtb.msa.noma.orchestrator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vtb.msa.noma.orchestrator.db.entity.Account;
import ru.vtb.msa.noma.orchestrator.db.entity.User;
import ru.vtb.msa.noma.orchestrator.db.repositorty.AccountRepository;
import ru.vtb.msa.noma.orchestrator.db.repositorty.TransactionRepository;
import ru.vtb.msa.noma.orchestrator.db.repositorty.UserRepository;
import ru.vtb.msa.noma.orchestrator.enums.AccountStatus;
import ru.vtb.msa.noma.orchestrator.enums.Currency;
import ru.vtb.msa.noma.orchestrator.exception.*;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.client.ComplexCheckClient;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckResponse;
import ru.vtb.msa.noma.orchestrator.mapper.DtoMapper;
import ru.vtb.msa.noma.orchestrator.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {


    private final ComplexCheckClient complexCheckClient;

    private final UserRepository userRepository;

    private final AccountRepository accountRepository;

    private final TransactionService transactionService;

    private final DtoMapper dtoMapper;

    private final TransactionRepository transactionRepository;

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

    @Transactional
    public void getTransactionsProcess(String xRequestId, TransactionRequest request) {
        validateHeader(xRequestId);

        if (accountExists(request.senderAccountId())) {
            throw new TransactionSenderNotFoundException("Счет отправителя не найден");
        }
        if (accountExists(request.receiverAccountId())) {
            throw new TransactionReceiverNotFoundException("Счет получателя не найден");
        }

        Account senderAccount = accountRepository.findById(request.senderAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Счет отправителя не найден"));

        Account receiverAccount = accountRepository.findById(request.receiverAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Счет получателя не найден"));

        if (!senderAccount.getCurrency().equals(receiverAccount.getCurrency())) {
            throw new CurrencyMisMatchException("Валюта отправителя и получателя не соответствует");
        }

        transactionService.executeTransaction(request, senderAccount, receiverAccount);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionByDate(String xRequestId, LocalDate date) {
        validateHeader(xRequestId);

        LocalDateTime from = date.atStartOfDay();                         // 2025-06-17T00:00
        LocalDateTime to   = date.atTime(LocalTime.MAX);                  // 2025-06-17T23:59:59.999999999
        List<TransactionDto> dtos = transactionRepository
                .findAllByTimestampBetween(from, to)
                .stream()
                .map(dtoMapper::fromEntityToDto)
                .toList();
        return new TransactionResponse(dtos);
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
        UserDto dto = request.user();   // или request.getUser() в зависимости от вашего request-контракта

        return User.builder()
                .name(dto.getName())
                .taxId(dto.getTaxId())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .registrationDate(ZonedDateTime.now())
                .build();
    }

    private boolean accountExists(UUID accountId) {
        return !accountRepository.existsById(accountId);
    }
}
