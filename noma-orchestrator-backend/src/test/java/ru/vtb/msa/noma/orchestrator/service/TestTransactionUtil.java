package ru.vtb.msa.noma.orchestrator.service;

import ru.vtb.msa.noma.orchestrator.db.entity.Account;
import ru.vtb.msa.noma.orchestrator.db.entity.User;
import ru.vtb.msa.noma.orchestrator.enums.AccountStatus;
import ru.vtb.msa.noma.orchestrator.enums.Currency;
import ru.vtb.msa.noma.orchestrator.model.TransactionRequest;

import java.time.ZonedDateTime;
import java.util.UUID;

public class TestTransactionUtil {

    public static User getUser() {
        User user = User.builder()
                .id(UUID.fromString("f9b2c8a4-1e33-47d8-9f0a-6b5d3e8c12f1"))
                .name("Vasya")
                .taxId("1234567890")
                .phone("+79034456789")
                .email("vasya@mail.ru")
                .registrationDate(ZonedDateTime.now())
                .version(0)
                .build();
        return user;
    }

    public static Account getAccountSender() {
        return Account.builder()
                .id(UUID.fromString("f9b2c8a4-1e33-47d8-9f0a-6b5d3e8c12f2"))
                .user(getUser())
                .balance(1000.0)
                .status(AccountStatus.ACTIVE)
                .createdAt(ZonedDateTime.now())
                .currency(Currency.RUB)
                .version(0)
                .build();
    }

    public static Account getAccountReceiver() {
        return Account.builder()
                .id(UUID.fromString("f0b2c8a4-1e33-47d8-9f0a-6b5d3e8c12f3"))
                .user(getUser())
                .balance(100.0)
                .status(AccountStatus.ACTIVE)
                .createdAt(ZonedDateTime.now())
                .currency(Currency.RUB)
                .version(0)
                .build();
    }

    public static TransactionRequest getTransactionRequest() {
        return  TransactionRequest.builder()
                .senderAccountId(UUID.fromString("f9b2c8a4-1e33-47d8-9f0a-6b5d3e8c12f1"))
                .receiverAccountId(UUID.fromString("b8b2c8a4-1e33-88a8-9f0a-6b5d3e8c12a3"))
                .amount(900.0)
                .currency("RUB")
                .build();
    }
}