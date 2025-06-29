package ru.vtb.msa.noma.orchestrator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vtb.msa.noma.orchestrator.db.entity.Account;
import ru.vtb.msa.noma.orchestrator.db.entity.User;
import ru.vtb.msa.noma.orchestrator.db.repositorty.AccountRepository;
import ru.vtb.msa.noma.orchestrator.db.repositorty.TransactionRepository;
import ru.vtb.msa.noma.orchestrator.db.repositorty.UserRepository;
import ru.vtb.msa.noma.orchestrator.exception.*;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.client.ComplexCheckClient;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckResponse;
import ru.vtb.msa.noma.orchestrator.mapper.DtoMapper;
import ru.vtb.msa.noma.orchestrator.model.*;
import ru.vtb.msa.noma.orchestrator.utils.ValidateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final ComplexCheckClient complexCheckClient;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final DtoMapper dtoMapper;

    public CreateAccountResponse createAccount(String xRequestId, CreateAccountRequest request) {
        ValidateUtil.validateXRequestIdHeader(xRequestId);

        ComplexCheckResponse response = complexCheckClient.complexCheck(request);
        complexCheckResponseProcessing(response);

        // сохраняем нового пользователя
        User user = dtoMapper.toUser(request.user());
        user = userRepository.save(user);

        // создаём и сохраняем аккаунт
        Account account = dtoMapper.toAccount(request, user);
        account = accountRepository.save(account);

        return new CreateAccountResponse(request.user(), account.getStatus().name());
    }

    @Transactional
    public void getTransactionsProcess(String xRequestId, TransactionRequest request) {
        ValidateUtil.validateXRequestIdHeader(xRequestId);

        UUID senderId = request.senderAccountId();
        UUID receiverId = request.receiverAccountId();

        if (!accountRepository.existsById(senderId)) {
            throw new TransactionSenderNotFoundException("Счет отправителя не найден");
        }
        if (!accountRepository.existsById(receiverId)) {
            throw new TransactionReceiverNotFoundException("Счет получателя не найден");
        }

        Account sender = accountRepository.findById(senderId)
                .orElseThrow(() -> new AccountNotFoundException("Счет отправителя не найден"));
        Account receiver = accountRepository.findById(receiverId)
                .orElseThrow(() -> new AccountNotFoundException("Счет получателя не найден"));

        if (!sender.getCurrency().equals(receiver.getCurrency())) {
            throw new CurrencyMisMatchException("Валюта отправителя и получателя не соответствует");
        }

        transactionService.executeTransaction(request, sender, receiver);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionByDate(String xRequestId, LocalDate date) {
        ValidateUtil.validateXRequestIdHeader(xRequestId);

        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.atTime(LocalTime.MAX);

        List<TransactionDto> dtos = transactionRepository
                .findAllByTimestampBetween(from, to)
                .stream()
                // вместо fromEntityToDto() — вызываем transactionToDto()
                .map(dtoMapper::transactionToDto)
                .collect(Collectors.toList());

        return new TransactionResponse(dtos);
    }

    public AccountDto getAccountById(String authorizationHeader, String uuid) {
        ValidateUtil.validateAuthorizationHeader(authorizationHeader);

        Account account = accountRepository.findById(UUID.fromString(uuid))
                .orElseThrow(() -> new AccountNotFoundException("Аккаунт не найден"));

        return dtoMapper.accountToDto(account);
    }

    public List<AccountDto> getAllAccounts(String authorizationHeader) {
        ValidateUtil.validateAuthorizationHeader(authorizationHeader);

        return accountRepository.findAll()
                .stream()
                .map(dtoMapper::accountToDto)
                .toList();
    }

    @Transactional
    public void deleteAccountById(String authorizationHeader, String id) {
        ValidateUtil.validateAuthorizationHeader(authorizationHeader);

        UUID uuid = UUID.fromString(id);

        Account account = accountRepository.findById(uuid)
                .orElseThrow(() -> new AccountNotFoundException("Аккаунт с id: " + id + " не найден"));

        accountRepository.delete(account);
    }


    private void complexCheckResponseProcessing(ComplexCheckResponse response) {
        var decision = response.requestResult().getDecision();
        switch (decision) {
            case DENY -> throw new ComplexCheckDenyException();
            case ARBITRATION -> throw new ComplexCheckArbitrationException();
        }
    }
}
