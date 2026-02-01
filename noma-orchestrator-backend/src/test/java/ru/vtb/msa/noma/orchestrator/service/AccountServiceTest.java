package ru.vtb.msa.noma.orchestrator.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vtb.msa.noma.orchestrator.db.entity.Account;
import ru.vtb.msa.noma.orchestrator.db.entity.Transaction;
import ru.vtb.msa.noma.orchestrator.db.entity.User;
import ru.vtb.msa.noma.orchestrator.db.repository.AccountRepository;
import ru.vtb.msa.noma.orchestrator.db.repository.TransactionRepository;
import ru.vtb.msa.noma.orchestrator.db.repository.UserRepository;
import ru.vtb.msa.noma.orchestrator.dto.TransferReceiptParams;
import ru.vtb.msa.noma.orchestrator.enums.AccountStatus;
import ru.vtb.msa.noma.orchestrator.enums.Currency;
import ru.vtb.msa.noma.orchestrator.enums.TransactionStatus;
import ru.vtb.msa.noma.orchestrator.exception.ComplexCheckArbitrationException;
import ru.vtb.msa.noma.orchestrator.exception.ComplexCheckDenyException;
import ru.vtb.msa.noma.orchestrator.exception.XRequestIdNotCorrectException;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.client.ComplexCheckClient;
import ru.vtb.msa.noma.orchestrator.integration.fraud.client.FraudClient;
import ru.vtb.msa.noma.orchestrator.integration.fraud.enums.FraudResponseStatus;
import ru.vtb.msa.noma.orchestrator.integration.fraud.pojo.FraudResponse;
import ru.vtb.msa.noma.orchestrator.integration.fraud.pojo.FraudResult;
import ru.vtb.msa.noma.orchestrator.mapper.DtoMapper;
import ru.vtb.msa.noma.orchestrator.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.vtb.msa.noma.orchestrator.service.TestUtil.authorizationHeader;
import static ru.vtb.msa.noma.orchestrator.service.TestUtil.validXRequestId;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private ComplexCheckClient complexCheckClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private DtoMapper dtoMapper;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private FraudClient fraudClient;

    @Mock
    private TransactionService transactionService;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldCreateAccountSuccess() {
        var validRequest = TestUtil.getValidRequest();
        var user = TestUtil.getUser();
        var account = TestUtil.getAccount();
        var allowResponse = TestUtil.getAllowResponse();
        when(complexCheckClient.complexCheck(validRequest)).thenReturn(allowResponse);
        when(dtoMapper.toUser(validRequest.user())).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(dtoMapper.toAccount(validRequest, user)).thenReturn(account);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        CreateAccountResponse response = accountService.createAccount(validXRequestId, validRequest);

        assertNotNull(response);
        assertEquals(validRequest.user(), response.user());
        assertEquals(AccountStatus.ACTIVE.name(), response.status());

        verify(complexCheckClient).complexCheck(validRequest);
        verify(userRepository).save(any(User.class));
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void shouldGetAccountByIdTest() {
        var user = TestUtil.getUserDto();
        var account = TestUtil.getAccount();
        String validRequestId = validXRequestId;
        String uuid = "f9b2c8a4-1e33-47d8-9f0a-6b5d3e8c12f1";
        AccountDto accountDto = AccountDto.builder()
                .accountId(UUID.fromString(uuid))
                .user(user)
                .currency(account.getCurrency().toString())
                .status(account.getStatus().toString())
                .createdAt(account.getCreatedAt())
                .build();

        when(accountRepository.findById(UUID.fromString(uuid))).thenReturn(Optional.of(account));
        when(dtoMapper.accountToDto(account)).thenReturn(accountDto);

        AccountDto accountDtoActual = accountService.getAccountById(validRequestId, uuid);

        Assertions.assertNotNull(accountDto);
        Assertions.assertEquals(accountDto, accountDtoActual);

        verify(accountRepository).findById(UUID.fromString(uuid));
        verify(dtoMapper).accountToDto(account);
    }

    @Test
    void getTransactionByDateTest() {
        String xRequestId = "X-Request-Id";
        LocalDate date = LocalDate.of(2025, 12, 29);
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.atTime(LocalTime.MAX);
        Transaction transaction1 = Transaction.builder()
                .id(UUID.fromString("e7b9f8a0-3c2d-4f5e-9a6b-1234567890ab"))
                .senderAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .receiverAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"))
                .amount(250.75)
                .currency("USD")
                .status(TransactionStatus.COMPLETED)
                .description("Арендная плата за июнь")
                .timestamp(LocalDateTime.of(2025, 12, 29, 10, 30, 0))
                .version(0)
                .build();

        Transaction transaction2 = Transaction.builder()
                .id(UUID.fromString("a1b2c3d4-5e6f-7a8b-9c0d-9e8f7a6b5c4d"))
                .senderAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174002"))
                .receiverAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174003"))
                .amount(1500.00)
                .currency("EUR")
                .status(TransactionStatus.PENDING)
                .description("Оплата за обучение")
                .timestamp(LocalDateTime.of(2025, 12, 29, 14, 45, 30))
                .version(0)
                .build();

        Transaction transaction3 = Transaction.builder()
                .id(UUID.fromString("f5e6d7c8-b9a0-1b2c-3d4e-5f6a7b8c9d0e"))
                .senderAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174004"))
                .receiverAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174005"))
                .amount(89.99)
                .currency("RUB")
                .status(TransactionStatus.FAILED)
                .description("Покупка в супермаркете")
                .timestamp(LocalDateTime.of(2025, 12, 29, 9, 15, 0))
                .version(1)
                .build();

        List<Transaction> transactionList = Arrays.asList(transaction1, transaction2, transaction3);

        TransactionDto transactionDto1 = TransactionDto.builder()
                .id(UUID.fromString("e7b9f8a0-3c2d-4f5e-9a6b-1234567890ab"))
                .senderAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .receiverAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"))
                .amount(250.75)
                .currency("USD")
                .description("Арендная плата за июнь")
                .timestamp(LocalDate.of(2025, 12, 29))
                .build();

        TransactionDto transactionDto2 = TransactionDto.builder()
                .id(UUID.fromString("a1b2c3d4-5e6f-7a8b-9c0d-9e8f7a6b5c4d"))
                .senderAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174002"))
                .receiverAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174003"))
                .amount(1500.00)
                .currency("EUR")
                .description("Оплата за обучение")
                .timestamp(LocalDate.of(2025, 12, 29))
                .build();

        TransactionDto transactionDto3 = TransactionDto.builder()
                .id(UUID.fromString("f5e6d7c8-b9a0-1b2c-3d4e-5f6a7b8c9d0e"))
                .senderAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174004"))
                .receiverAccountId(UUID.fromString("123e4567-e89b-12d3-a456-426614174005"))
                .amount(89.99)
                .currency("RUB")
                .description("Покупка в супермаркете")
                .timestamp(LocalDate.of(2025, 12, 29))
                .build();

        List<TransactionDto> dtoList = Arrays.asList(transactionDto1, transactionDto2, transactionDto3);

        when(transactionRepository.findAllByTimestampBetween(from, to)).thenReturn(transactionList);

        doReturn(transactionDto1)
                .doReturn(transactionDto2)
                .doReturn(transactionDto3)
                .when(dtoMapper).transactionToDto(any(Transaction.class));

        TransactionResponse expected = new TransactionResponse(dtoList);
        TransactionResponse actualDtoList = accountService.getTransactionByDate(xRequestId, date);

        Assertions.assertNotNull(actualDtoList);
        Assertions.assertEquals(expected, actualDtoList);

        verify(transactionRepository).findAllByTimestampBetween(from, to);
        verify(dtoMapper, times(3)).transactionToDto(any(Transaction.class));
    }

    @Test
    void shouldTransactionsProcessSuccess() {
        var xRequestId = validXRequestId;
        var user = TestUtil.getUser();
        var updatedUser = TestUtil.getSavedUser();
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        var request = TestUtil.getTransactionRequest(senderId, receiverId);
        Account senderBefore = Account.builder()
                .id(senderId)
                .user(user)
                .balance(500.0)
                .status(AccountStatus.ACTIVE)
                .currency(Currency.RUB)
                .createdAt(ZonedDateTime.now())
                .version(0)
                .build();

        Account receiverBefore = Account.builder()
                .id(receiverId)
                .user(user)
                .balance(200.0)
                .status(AccountStatus.ACTIVE)
                .currency(Currency.RUB)
                .createdAt(ZonedDateTime.now())
                .version(0)
                .build();

        Account senderAfter = Account.builder()
                .id(senderId)
                .user(updatedUser)
                .balance(400.0)
                .status(AccountStatus.ACTIVE)
                .currency(Currency.RUB)
                .createdAt(senderBefore.getCreatedAt())
                .version(1)
                .build();

        Account receiverAfter = Account.builder()
                .id(receiverId)
                .user(updatedUser)
                .balance(300.0)
                .status(AccountStatus.ACTIVE)
                .currency(Currency.RUB)
                .createdAt(receiverBefore.getCreatedAt())
                .version(1)
                .build();

        FraudResult senderFraudResult = new FraudResult(
                senderId,
                FraudResponseStatus.SAFE,
                "Transaction allowed for sender"
        );

        FraudResult receiverFraudResult = new FraudResult(
                receiverId,
                FraudResponseStatus.SAFE,
                "Transaction allowed for receiver"
        );

        FraudResponse fraudResponse = FraudResponse.builder()
                .results(List.of(senderFraudResult, receiverFraudResult))
                .build();

        TransferReceiptParams receiptParams = TransferReceiptParams.builder()
                .senderAccountId(senderId)
                .receiverAccountId(receiverId)
                .amount(100.0)
                .currency("RUB")
                .description("Перевод")
                .timestamp(LocalDateTime.now().toString())
                .senderBalanceAfter(400.0)
                .receiverBalanceAfter(300.0)
                .build();

        byte[] expectedPdf = new byte[]{1, 2, 3, 4, 5};

        when(accountRepository.findById(senderId))
                .thenReturn(Optional.of(senderBefore))
                .thenReturn(Optional.of(senderAfter));
        when(accountRepository.findById(receiverId))
                .thenReturn(Optional.of(receiverBefore))
                .thenReturn(Optional.of(receiverAfter));
        when(fraudClient.checkFraud(senderBefore, receiverBefore))
                .thenReturn(fraudResponse);
        doNothing().when(transactionService).executeTransaction(request);
        when(dtoMapper.transferReceiptToDto(request, senderAfter, receiverAfter))
                .thenReturn(receiptParams);
        when(reportService.generateTransferReceiptPdf(receiptParams, null))
                .thenReturn(expectedPdf);

        byte[] result = accountService.transactionsProcess(xRequestId, request);

        assertNotNull(result);
        assertArrayEquals(expectedPdf, result);

        verify(accountRepository, times(2)).findById(senderId);
        verify(accountRepository, times(2)).findById(receiverId);
        verify(fraudClient, times(1)).checkFraud(senderBefore, receiverBefore);
        verify(transactionService, times(1)).executeTransaction(request);
        verify(dtoMapper, times(1)).transferReceiptToDto(request, senderAfter, receiverAfter);
        verify(reportService, times(1)).generateTransferReceiptPdf(receiptParams, null);
    }

    @Test
    void getAccountByIdTest() {
        var account = TestUtil.getAccount();
        String uuid = "f9b2c8a4-1e33-47d8-9f0a-6b5d3e8c12f2";
        UserDto userDto = UserDto.builder()
                .name("Иван Иванов")
                .taxId("123456789012")
                .phone("+79991234567")
                .email("ivanov@example.com")
                .build();
        AccountDto accountDto = AccountDto.builder()
                .accountId(UUID.fromString("f9b2c8a4-1e33-47d8-9f0a-6b5d3e8c12f2"))
                .user(userDto)
                .balance(15000.50)
                .currency("RUB")
                .status("ACTIVE")
                .createdAt(ZonedDateTime.parse("2024-01-15T10:30:00+03:00"))
                .build();

        when(accountRepository.findById(UUID.fromString(uuid))).thenReturn(Optional.ofNullable(account));
        when(dtoMapper.accountToDto(account)).thenReturn(accountDto);

        AccountDto accountDtoActual = accountService.getAccountById(authorizationHeader, uuid);

        Assertions.assertEquals(accountDto, accountDtoActual);
    }

    @Test
    void shouldGetAllAccounts() {
        List<Account> accounts = TestUtil.getAccountList();
        List<AccountDto> accountDtos = TestUtil.getAccountDtoList();

        when(accountRepository.findAll()).thenReturn(accounts);
        when(dtoMapper.accountToDto(accounts.get(0))).thenReturn(accountDtos.get(0));
        when(dtoMapper.accountToDto(accounts.get(1))).thenReturn(accountDtos.get(1));
        when(dtoMapper.accountToDto(accounts.get(2))).thenReturn(accountDtos.get(2));

        List<AccountDto> result = accountService.getAllAccounts(authorizationHeader);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(accountDtos, result);

        verify(accountRepository, times(1)).findAll();
        verify(dtoMapper, times(3)).accountToDto(any(Account.class));
    }

    @Test
    void shouldDeleteAccount() {
        String id = "f9b2c8a4-1e33-47d8-9f0a-6b5d3e8c12f2";
        String authorizationHeader = "123456";

        accountService.deleteAccount(authorizationHeader, id);

        verify(accountRepository, times(1)).deleteById(UUID.fromString(id));
    }

    @Test
    void shouldUpdateAccount() {
        String id = "f9b2c8a4-1e33-47d8-9f0a-6b5d3e8c12f2";
        String authorizationHeader = "123456";
        UpdateAccountRequest updateAccountRequest = TestUtil.getUpdateAccountRequest();
        var savedUser = TestUtil.getSavedUser();
        var account = TestUtil.getAccount();
        var updateAccount = TestUtil.getUpdateAccount();
        updateAccount.setUser(savedUser);
        UserDto userDto1 = UserDto.builder()
                .name("Vasya")
                .taxId("11111111")
                .phone("+79034456789")
                .email("vasya@mail.ru")
                .build();
        AccountDto accountDto1 = AccountDto.builder()
                .accountId(UUID.fromString("f9b2c8a4-1e33-47d8-9f0a-6b5d3e8c12f2"))
                .user(userDto1)
                .currency("RUB")
                .status("ACTIVE")
                .createdAt(ZonedDateTime.now())
                .build();
        when(accountRepository.findById(UUID.fromString(id))).thenReturn(Optional.of(account));

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(dtoMapper.toAccount(any(UpdateAccountRequest.class), any(User.class))).thenReturn(updateAccount);
        when(accountRepository.save(updateAccount)).thenReturn(updateAccount);
        when(dtoMapper.accountToDto(updateAccount)).thenReturn(accountDto1);

        UpdateAccountResponse updateAccountResponse = accountService.updateAccount(
                authorizationHeader, id, updateAccountRequest);

        Assertions.assertEquals(accountDto1, updateAccountResponse.account());

        verify(accountRepository, times(1)).findById(UUID.fromString(id));
        verify(userRepository, times(1)).save(any(User.class));
        verify(accountRepository, times(1)).save(updateAccount);
    }


    @Test
    void shouldThrowExceptionWhenHeaderIsEmpty() {
        var validRequest = TestUtil.getValidRequest();
        assertThrows(XRequestIdNotCorrectException.class,
                () -> accountService.createAccount(" ", validRequest));
        verifyNoInteractions(complexCheckClient, userRepository, accountRepository);
    }

    @Test
    void shouldThrowExceptionWhenHeaderIsNull() {
        var validRequest = TestUtil.getValidRequest();
        assertThrows(XRequestIdNotCorrectException.class,
                () -> accountService.createAccount(null, validRequest));
        verifyNoInteractions(complexCheckClient, userRepository, accountRepository);
    }

    @Test
    void shouldThrowExceptionWhenResultComplexCheckIsDeny() {
        var validRequest = TestUtil.getValidRequest();
        var denyResponse = TestUtil.getDenyResponse();
        when(complexCheckClient.complexCheck(validRequest)).thenReturn(denyResponse);
        assertThrows(ComplexCheckDenyException.class,
                () -> accountService.createAccount(validXRequestId, validRequest));
        verify(complexCheckClient).complexCheck(validRequest);
        verifyNoInteractions(userRepository, accountRepository);
    }

    @Test
    void shouldThrowExceptionWhenResultComplexCheckIsArbitration() {
        var validRequest = TestUtil.getValidRequest();
        var arbitrationResponse = TestUtil.getArbitrationResponse();
        when(complexCheckClient.complexCheck(validRequest)).thenReturn(arbitrationResponse);
        assertThrows(ComplexCheckArbitrationException.class,
                () -> accountService.createAccount(validXRequestId, validRequest));
        verify(complexCheckClient).complexCheck(validRequest);
        verifyNoInteractions(userRepository, accountRepository);
    }
}
