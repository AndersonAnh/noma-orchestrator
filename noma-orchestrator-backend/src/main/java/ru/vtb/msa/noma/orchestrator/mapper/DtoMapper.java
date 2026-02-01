package ru.vtb.msa.noma.orchestrator.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import ru.vtb.msa.noma.orchestrator.db.entity.Account;
import ru.vtb.msa.noma.orchestrator.db.entity.InsuranceLife;
import ru.vtb.msa.noma.orchestrator.db.entity.Transaction;
import ru.vtb.msa.noma.orchestrator.db.entity.User;
import ru.vtb.msa.noma.orchestrator.dto.TransferReceiptParams;
import ru.vtb.msa.noma.orchestrator.enums.Currency;
import ru.vtb.msa.noma.orchestrator.enums.TransactionStatus;
import ru.vtb.msa.noma.orchestrator.model.*;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        imports = {ZonedDateTime.class, Currency.class, LocalDate.class, TransactionStatus.class, LocalDateTime.class}
)
public interface DtoMapper {

    /* ---------- TRANSACTION: entity → DTO ---------- */

    @Mapping(target = "timestamp",
            expression = "java(entity.getTimestamp().toLocalDate())")
    TransactionDto transactionToDto(Transaction entity);

    /* ---------- USER: DTO ↔ entity ---------- */

    // DTO → entity (создание)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationDate", expression = "java(ZonedDateTime.now())")
    @Mapping(target = "version", ignore = true)
    User toUser(UserDto dto);

    // Обновление существующего User из DTO
    void updateUserFromDto(UserDto dto, @MappingTarget User entity);

    // entity → DTO
    UserDto userToDto(User user);

    /* ---------- ACCOUNT: DTO → entity ---------- */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "balance", source = "request.balance")
    @Mapping(target = "currency",
            expression = "java(Currency.valueOf(request.currency().toUpperCase()))")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt",
            expression = "java(ZonedDateTime.now())")
    @Mapping(target = "version", ignore = true)
    Account toAccount(CreateAccountRequest request, User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "balance", source = "request.account.balance")
    @Mapping(target = "currency",
            expression = "java(Currency.valueOf(request.account().currency().toUpperCase()))")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "updatedAt",
            expression = "java(ZonedDateTime.now())")
    @Mapping(target = "version", ignore = true)
    Account toAccount(UpdateAccountRequest request, User user);

    /* ---------- ACCOUNT: entity → DTO ---------- */

    // 🟢 Основное исправление — передаём UUID счёта
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "user", source = "account.user")
    @Mapping(target = "currency",
            expression = "java(account.getCurrency().name())")
    @Mapping(target = "status",
            expression = "java(account.getStatus().name())")
    @Mapping(target = "createdAt", source = "account.createdAt")
    AccountDto accountToDto(Account account);

    @Mapping(target = "senderAccountId", source = "transactionRequest.senderAccountId")
    @Mapping(target = "receiverAccountId", source = "transactionRequest.receiverAccountId")
    @Mapping(target = "amount", source = "transactionRequest.amount")
    @Mapping(target = "currency", source = "transactionRequest.currency")
    @Mapping(target = "description", source = "transactionRequest.description")
    @Mapping(target = "timestamp", expression = "java(LocalDate.now().toString())")
    @Mapping(target = "senderBalanceAfter", source = "senderAccount.balance")
    @Mapping(target = "receiverBalanceAfter", source = " receiverAccount.balance")
    TransferReceiptParams transferReceiptToDto(TransactionRequest transactionRequest, Account senderAccount, Account receiverAccount);

    @Mapping(target = "senderAccountId", source = "senderAccountId")
    @Mapping(target = "receiverAccountId", source = "receiverAccountId")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "status", expression = "java(TransactionStatus.COMPLETED)")
    @Mapping(target = "timestamp", expression = "java(LocalDateTime.now())")
    @Mapping(target = "description", source = "description")
    Transaction toTransaction(TransactionRequest transactionRequest);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyNumber", source = "request.policyNumber")
    @Mapping(target = "clientId", source = "request.clientId")
    @Mapping(target = "agentId", source = "request.agentId")
    @Mapping(target = "coverageType", source = "request.coverageType")
    @Mapping(target = "specialConditions", source = "request.specialConditions")
    @Mapping(target = "policyStatus", expression = "java(ru.vtb.msa.noma.orchestrator.enums.PolicyStatus.DRAFT)")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "startDate", expression = "java(LocalDate.now())")
    @Mapping(target = "endDate", expression = "java(LocalDate.now().plusMonths(request.termInMonths()))")
    @Mapping(target = "insuredAmount", source = "insuredAmount")
    @Mapping(target = "premiumAmount", source = "premiumAmount")
    @Mapping(target = "createdBy", constant = "system")
    @Mapping(target = "updatedBy", constant = "system")
    InsuranceLife toInsurance(InsuranceLifeRequest request, BigDecimal insuredAmount, BigDecimal premiumAmount);

    @Mapping(target = "baseInsuredAmount", source = "insuredAmount")
    InsuranceLifePolicyResponse toInsuranceResponse(InsuranceLife entity);
}