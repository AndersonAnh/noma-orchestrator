package ru.vtb.msa.noma.orchestrator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vtb.msa.noma.orchestrator.db.entity.Account;
import ru.vtb.msa.noma.orchestrator.db.entity.BicEntity;
import ru.vtb.msa.noma.orchestrator.db.entity.User;
import ru.vtb.msa.noma.orchestrator.db.repository.AccountRepository;
import ru.vtb.msa.noma.orchestrator.db.repository.BicRepository;
import ru.vtb.msa.noma.orchestrator.db.repository.TransactionRepository;
import ru.vtb.msa.noma.orchestrator.db.repository.UserRepository;
import ru.vtb.msa.noma.orchestrator.dto.TransferReceiptParams;
import ru.vtb.msa.noma.orchestrator.exception.*;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.client.ComplexCheckClient;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckResponse;
import ru.vtb.msa.noma.orchestrator.integration.fraud.client.FraudClient;
import ru.vtb.msa.noma.orchestrator.integration.fraud.pojo.FraudResponse;
import ru.vtb.msa.noma.orchestrator.integration.infoservice.sender.InfoServiceSender;
import ru.vtb.msa.noma.orchestrator.mapper.DtoMapper;
import ru.vtb.msa.noma.orchestrator.model.*;
import ru.vtb.msa.noma.orchestrator.utils.CheckResponseUtil;
import ru.vtb.msa.noma.orchestrator.utils.JsonUtil;
import ru.vtb.msa.noma.orchestrator.utils.ValidateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final ComplexCheckClient complexCheckClient;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final DtoMapper dtoMapper;
    private final FraudClient fraudClient;
    private final InfoServiceSender infoServiceSender;
    private final BicCatalogService bicCatalogService;
    private final BicRepository bicRepository;
    private final ReportService reportService;

    public CreateAccountResponse createAccount(String xRequestId, CreateAccountRequest request) {
        ValidateUtil.validateXRequestIdHeader(xRequestId);

        ComplexCheckResponse response = complexCheckClient.complexCheck(request);
        CheckResponseUtil.complexCheckResponseProcessing(response);
        log.debug("Ответ из сервиса комплексной проверки {}", JsonUtil.toMaskedJson(response));

        User user = dtoMapper.toUser(request.user());
        user = userRepository.save(user);

        Account account = dtoMapper.toAccount(request, user);
        account = accountRepository.save(account);
        log.info("New Account is Created: {}", JsonUtil.toMaskedJson(account));
        return new CreateAccountResponse(request.user(), account.getStatus().name());
    }

    @Transactional
    public byte[] transactionsProcess(String xRequestId, TransactionRequest request) {
        ValidateUtil.validateXRequestIdHeader(xRequestId);

        UUID senderId = request.senderAccountId();
        UUID receiverId = request.receiverAccountId();

        Account sender = accountRepository.findById(senderId)
                .orElseThrow(() -> new TransactionSenderNotFoundException("Счет отправителя не найден"));
        Account receiver = accountRepository.findById(receiverId)
                .orElseThrow(() -> new TransactionReceiverNotFoundException("Счет получателя не найден"));

        FraudResponse fraudResponse = fraudClient.checkFraud(sender, receiver);

        CheckResponseUtil.handleFraudResponse(fraudResponse);
        log.debug("Ответ из сервиса проверки на мошенничество {}", JsonUtil.toMaskedJson(fraudResponse));

        transactionService.executeTransaction(request);

        Account senderAfter = accountRepository.findById(senderId)
                .orElseThrow(() -> new TransactionSenderNotFoundException("Счет отправителя не найден"));
        Account receiverAfter = accountRepository.findById(receiverId)
                .orElseThrow(() -> new TransactionReceiverNotFoundException("Счет получателя не найден"));

        TransferReceiptParams transferReceiptParams = dtoMapper.transferReceiptToDto(request, senderAfter, receiverAfter);

        return reportService.generateTransferReceiptPdf(transferReceiptParams, null);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionByDate(String xRequestId, LocalDate date) {//xRequestId,25.12.2025
        ValidateUtil.validateXRequestIdHeader(xRequestId);//валидация xRequestId

        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.atTime(LocalTime.MAX);

        List<TransactionDto> dtos = transactionRepository
                .findAllByTimestampBetween(from, to)
                .stream()
                .map(dtoMapper::transactionToDto)
                .collect(Collectors.toList());

        return new TransactionResponse(dtos);
    }

    public AccountDto getAccountById(String authorizationHeader, String uuid) {
        ValidateUtil.validateAuthorizationHeader(authorizationHeader);

        return accountRepository.findById(UUID.fromString(uuid))
                .map(dtoMapper::accountToDto)
                .orElseThrow(() -> new AccountNotFoundException("Аккаунт не найден"));
    }

    public List<AccountDto> getAllAccounts(String authorizationHeader) {
        ValidateUtil.validateAuthorizationHeader(authorizationHeader);

        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(dtoMapper::accountToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAccount(String authorizationHeader, String id) {
        ValidateUtil.validateAuthorizationHeader(authorizationHeader);
        accountRepository.deleteById(UUID.fromString(id));
    }

    @Transactional
    public UpdateAccountResponse updateAccount(String authorizationHeader, String id, UpdateAccountRequest request) {
        ValidateUtil.validateAuthorizationHeader(authorizationHeader);

        Account account = accountRepository.findById(UUID.fromString(id))
                .orElseThrow(() ->
                        new AccountNotFoundException("Аккаунт с id: " + id + " не найден")
                );

        User user = account.getUser();
        userRepository.save(user);

        Account updatedAccount = dtoMapper.toAccount(request, user);
        accountRepository.save(updatedAccount);

        return new UpdateAccountResponse(
                dtoMapper.accountToDto(updatedAccount)
        );
    }

    public void accountUpdatedEvent(AccountUpdatedEventRequest account) {
        infoServiceSender.send(account);
    }

    public BanksAndTypesResponseDto getBanksAndTypes() {
        BicEntity bicEntity = bicRepository.findTopByOrderByUploadedAtDesc()
                .orElseThrow(() -> new BicNotFoundException("BIC каталог не найден."));
        final String xmlBanksBicCatalog = bicEntity.getBicCatalog();
        List<BicBankDto> banks = bicCatalogService.parseBanks(xmlBanksBicCatalog);
        List<AccountTransferTypeDto> types = bicCatalogService.getTransferNominalTypes();
        return new BanksAndTypesResponseDto(banks, types);
    }
}