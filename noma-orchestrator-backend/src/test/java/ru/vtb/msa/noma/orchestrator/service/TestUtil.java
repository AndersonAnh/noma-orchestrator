package ru.vtb.msa.noma.orchestrator.service;

import ru.vtb.msa.noma.orchestrator.db.entity.Account;
import ru.vtb.msa.noma.orchestrator.db.entity.User;
import ru.vtb.msa.noma.orchestrator.enums.AccountStatus;
import ru.vtb.msa.noma.orchestrator.enums.Currency;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.enums.ComplexCheckResult;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckRequestResult;
import ru.vtb.msa.noma.orchestrator.integration.complexcheck.pojo.ComplexCheckResponse;
import ru.vtb.msa.noma.orchestrator.model.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestUtil {

    public static String validXRequestId = "XReq1";
    public static String authorizationHeader = "123456";

    public static UserDto getUserDto() {
        UserDto user = UserDto.builder()
                .name("Vasya")
                .taxId("11111111")
                .phone("+79034456789")
                .email("vasya@mail.ru")
                .build();
        return user;
    }

    public static User getUser() {
        User user = User.builder()
                .id(UUID.fromString("f9b2c8a4-1e33-47d8-9f0a-6b5d3e8c12f1"))
                .name("Vasya")
                .taxId("1234567890") // 10 цифр - соответствует regexp
                .phone("+79034456789")
                .email("vasya@mail.ru")
                .registrationDate(ZonedDateTime.now())
                .version(0)
                .build();
        return user;
    }

    public static User getSavedUser() {
        User user = User.builder()
                .id(UUID.fromString("f9b2c8a4-1e33-47d8-9f0a-6b5d3e8c12f1"))
                .name("Kolya")
                .taxId("1234567891") // 10 цифр - соответствует regexp
                .phone("+79032458907")
                .email("kolya@mail.ru")
                .registrationDate(ZonedDateTime.now())
                .version(0)
                .build();
        return user;
    }

    public static CreateAccountRequest getValidRequest() {
        UserDto user = getUserDto();
        return new CreateAccountRequest(user, 100.0, "RUB");
    }

    public static Account getAccount() {
        Account account = Account.builder()
                .id(UUID.fromString("f9b2c8a4-1e33-47d8-9f0a-6b5d3e8c12f2"))
                .user(getUser())
                .balance(100.0)
                .status(AccountStatus.ACTIVE)
                .createdAt(ZonedDateTime.now())
                .currency(Currency.RUB)
                .version(0)
                .build();
        return account;
    }

    public static TransactionRequest getTransactionRequest(UUID senderId, UUID receiverId) {
        TransactionRequest request = TransactionRequest.builder()
                .senderAccountId(senderId)
                .receiverAccountId(receiverId)
                .amount(100.0)
                .build();
        return request;
    }

    public static Account getUpdateAccount() {
        Account updateAccount = Account.builder()
                .id(UUID.fromString("f9b2c8a4-1e33-47d8-9f0a-6b5d3e8c12f2"))
                .user(getUser())  // Связь с первым пользователем
                .balance(50.0)
                .status(AccountStatus.ACTIVE)
                .createdAt(ZonedDateTime.now())
                .currency(Currency.RUB)
                .version(0)
                .build();
        return updateAccount;
    }

    public static ComplexCheckRequestResult getAllowResult() {
        ComplexCheckRequestResult allowResult = new ComplexCheckRequestResult();
        allowResult.setDecision(ComplexCheckResult.ALLOW);
        return allowResult;
    }

    public static ComplexCheckRequestResult getDenyResult() {
        ComplexCheckRequestResult denyResult = new ComplexCheckRequestResult();
        denyResult.setDecision(ComplexCheckResult.DENY);
        return denyResult;
    }

    public static ComplexCheckRequestResult getArbitrationResult() {
        ComplexCheckRequestResult arbitrationResult = new ComplexCheckRequestResult();
        arbitrationResult.setDecision(ComplexCheckResult.ARBITRATION);
        return arbitrationResult;
    }

    public static ComplexCheckResponse getAllowResponse() {
        return new ComplexCheckResponse(getAllowResult());
    }

    public static ComplexCheckResponse getDenyResponse() {
        return new ComplexCheckResponse(getDenyResult());
    }

    public static ComplexCheckResponse getArbitrationResponse() {
        return new ComplexCheckResponse(getArbitrationResult());
    }

    public static List<Account> getAccountList() {

        User user1 = User.builder()
                .id(UUID.fromString("f9b2c8a4-1e33-47d8-9f0a-6b5d3e8c12f1"))
                .name("Vasya")
                .taxId("11111111")
                .phone("+79034456789")
                .email("vasya@mail.ru")
                .registrationDate(ZonedDateTime.now())
                .version(0)
                .build();

        User user2 = User.builder()
                .id(UUID.fromString("a2b3c4d5-e6f7-8901-abcd-ef2345678901"))
                .name("Petya")
                .taxId("22222222")
                .phone("+79045551234")
                .email("petya@gmail.com")
                .registrationDate(ZonedDateTime.now().minusDays(10))
                .version(0)
                .build();

        User user3 = User.builder()
                .id(UUID.fromString("b3c4d5e6-f789-0123-abcd-ef3456789012"))
                .name("Masha")
                .taxId("33333333")
                .phone("+79056667890")
                .email("masha@yandex.ru")
                .registrationDate(ZonedDateTime.now().minusMonths(1))
                .version(0)
                .build();

        Account account1 = Account.builder()
                .id(UUID.fromString("f9b2c8a4-1e33-47d8-9f0a-6b5d3e8c12f2"))
                .user(user1)  // Связь с первым пользователем
                .balance(100.0)
                .status(AccountStatus.ACTIVE)
                .createdAt(ZonedDateTime.now())
                .currency(Currency.RUB)
                .version(0)
                .build();

        Account account2 = Account.builder()
                .id(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"))
                .user(user2)  // Связь со вторым пользователем
                .balance(500.0)
                .status(AccountStatus.ACTIVE)
                .createdAt(ZonedDateTime.now().minusDays(1))
                .currency(Currency.USD)
                .version(0)
                .build();

        Account account3 = Account.builder()
                .id(UUID.randomUUID())
                .user(user3)  // Связь с третьим пользователем
                .balance(0.0)
                .status(AccountStatus.BLOCKED)
                .createdAt(ZonedDateTime.now().minusMonths(1))
                .currency(Currency.EUR)
                .version(0)
                .build();

        List<Account> accounts = new ArrayList<>();
        accounts.add(account1);
        accounts.add(account2);
        accounts.add(account3);

        return accounts;
    }

    public static List<AccountDto> getAccountDtoList() {
        UserDto userDto1 = UserDto.builder()
                .name("Vasya")
                .taxId("11111111")
                .phone("+79034456789")
                .email("vasya@mail.ru")
                .build();

        UserDto userDto2 = UserDto.builder()
                .name("Petya")
                .taxId("22222222")
                .phone("+79045551234")
                .email("petya@gmail.com")
                .build();

        UserDto userDto3 = UserDto.builder()
                .name("Masha")
                .taxId("33333333")
                .phone("+79056667890")
                .email("masha@yandex.ru")
                .build();

        List<AccountDto> accountDtos = new ArrayList<>();

        AccountDto accountDto1 = AccountDto.builder()
                .accountId(UUID.fromString("f9b2c8a4-1e33-47d8-9f0a-6b5d3e8c12f2"))
                .user(userDto1)  // Используем UserDto вместо User entity
                .currency("RUB")
                .status("ACTIVE")
                .createdAt(ZonedDateTime.now())
                .build();

        AccountDto accountDto2 = AccountDto.builder()
                .accountId(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"))
                .user(userDto2)
                .currency("USD")
                .status("ACTIVE")
                .createdAt(ZonedDateTime.now().minusDays(1))
                .build();

        AccountDto accountDto3 = AccountDto.builder()
                .accountId(UUID.randomUUID())
                .user(userDto3)
                .currency("EUR")
                .status("SUSPENDED")
                .createdAt(ZonedDateTime.now().minusMonths(1))
                .build();

        accountDtos.add(accountDto1);
        accountDtos.add(accountDto2);
        accountDtos.add(accountDto3);
        return accountDtos;
    }

    public static UpdateAccountRequest getUpdateAccountRequest() {
        List<UpdateAccountRequest> updateRequests = new ArrayList<>();
        UpdateAccountRequest request1 = new UpdateAccountRequest(
                AccountDto.builder()
                        .accountId(UUID.fromString("f9b2c8a4-1e33-47d8-9f0a-6b5d3e8c12f2"))
                        .user(UserDto.builder()
                                .name("Vasya")
                                .taxId("11111111")
                                .phone("+79034456789")
                                .email("vasya@mail.ru")
                                .build())
                        .balance(150.0)
                        .currency("RUB")
                        .status("ACTIVE")
                        .createdAt(ZonedDateTime.now())
                        .build()
        );

        UpdateAccountRequest request2 = new UpdateAccountRequest(
                AccountDto.builder()
                        .accountId(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"))
                        .user(UserDto.builder()
                                .name("Petya")
                                .taxId("22222222")
                                .phone("+79045551234")
                                .email("petya@gmail.com")
                                .build())
                        .balance(750.0)
                        .currency("USD")
                        .status("ACTIVE")
                        .createdAt(ZonedDateTime.now().minusDays(1))
                        .build()
        );

        UpdateAccountRequest request3 = new UpdateAccountRequest(
                AccountDto.builder()
                        .accountId(UUID.randomUUID())
                        .user(UserDto.builder()
                                .name("Masha")
                                .taxId("33333333")
                                .phone("+79056667890")
                                .email("masha@yandex.ru")
                                .build())
                        .balance(50.0)
                        .currency("EUR")
                        .status("SUSPENDED")
                        .createdAt(ZonedDateTime.now().minusMonths(1))
                        .build()
        );

        updateRequests.add(request1);
        updateRequests.add(request2);
        updateRequests.add(request3);

        return request1;
    }
}

