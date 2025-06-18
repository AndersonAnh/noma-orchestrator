package ru.vtb.msa.noma.orchestrator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.enums.ComplexCheckResult;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckRequestResult;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckResponse;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountRequest;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountResponse;
import ru.vtb.msa.noma.orchestrator.model.UserDto;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private ComplexCheckClient complexCheckClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private String validXRequestId;
    private CreateAccountRequest validRequest;
    private ComplexCheckResponse allowResponse;
    private ComplexCheckResponse denyResponse;
    private ComplexCheckResponse arbitrationResponse;
    private User savedUser;
    private Account savedAccount;

    @BeforeEach
    void setUp() {
        validXRequestId = "X-Request-Id";
        UserDto user = UserDto.builder()
                .name("Vasya")
                .taxId("11111111")
                .phone("+79034456789")
                .email("vasya@mail.ru")
                .build();
        validRequest = new CreateAccountRequest(user, 100.0, "RUB");
        savedUser = User.builder()
                .id(UUID.randomUUID())
                .name("Vasya")
                .taxId("11111111")
                .phone("+79034456789")
                .email("vasya@mail.ru")
                .registrationDate(ZonedDateTime.now())
                .version(0)
                .build();
        savedAccount = Account.builder()
                .id(UUID.randomUUID())
                .user(savedUser)
                .balance(100.0)
                .status(AccountStatus.ACTIVE)
                .createdAt(ZonedDateTime.now())
                .currency(Currency.RUB)
                .version(0)
                .build();
        ComplexCheckRequestResult allowResult = new ComplexCheckRequestResult();
        allowResult.setDecision(ComplexCheckResult.ALLOW);
        allowResponse = ComplexCheckResponse.builder()
                .requestResult(allowResult)
                .build();
        ComplexCheckRequestResult denyResult = new ComplexCheckRequestResult();
        denyResult.setDecision(ComplexCheckResult.DENY);
        denyResponse = ComplexCheckResponse.builder()
                .requestResult(denyResult)
                .build();
        ComplexCheckRequestResult arbitrationResult = new ComplexCheckRequestResult();
        arbitrationResult.setDecision(ComplexCheckResult.ARBITRATION);
        arbitrationResponse = ComplexCheckResponse.builder()
                .requestResult(arbitrationResult)
                .build();
    }

    @Test
    void shouldCreateAccountSuccess() {

        when(complexCheckClient.complexCheck(validRequest)).thenReturn(allowResponse);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        CreateAccountResponse response = accountService.createAccount(validXRequestId, validRequest);

        assertNotNull(response);
        assertEquals(validRequest.user(), response.user());
        assertEquals(response.status(), AccountStatus.ACTIVE.name());

        verify(complexCheckClient).complexCheck(validRequest);
        verify(userRepository).save(any(User.class));
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void shouldThrowExceptionWhenHeaderIsEmpty() {

        Exception exception = assertThrows(XRequestIdNotCorrectException.class,
                () -> accountService.createAccount(" ", validRequest));

        assertEquals(exception.getMessage(), "Заголовок xRequestId обязателен");

        verifyNoInteractions(complexCheckClient, userRepository, accountRepository);
    }

    @Test
    void shouldThrowExceptionWhenHeaderIsNull() {

        Exception exception = assertThrows(XRequestIdNotCorrectException.class,
                () -> accountService.createAccount(null, validRequest));

        assertEquals(exception.getMessage(), "Заголовок xRequestId обязателен");

        verifyNoInteractions(complexCheckClient, userRepository, accountRepository);
    }

    @Test
    void shouldThrowExceptionWhenResultComplexCheckIsDeny() {

        when(complexCheckClient.complexCheck(validRequest)).thenReturn(denyResponse);

        assertThrows(ComplexCheckDenyException.class, () -> accountService.createAccount(
                validXRequestId, validRequest));

        verify(complexCheckClient).complexCheck(validRequest);
        verifyNoInteractions(userRepository, accountRepository);
    }

    @Test
    void shouldThrowExceptionWhenResultComplexCheckIsArbitration() {

        when(complexCheckClient.complexCheck(validRequest)).thenReturn(arbitrationResponse);

        assertThrows(ComplexCheckArbitrationException.class, () -> accountService.createAccount(
                validXRequestId, validRequest
        ));

        verify(complexCheckClient).complexCheck(validRequest);
        verifyNoInteractions(userRepository, accountRepository);
    }
}
