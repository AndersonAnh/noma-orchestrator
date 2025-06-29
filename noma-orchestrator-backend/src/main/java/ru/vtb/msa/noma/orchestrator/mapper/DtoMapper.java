package ru.vtb.msa.noma.orchestrator.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.vtb.msa.noma.orchestrator.db.entity.*;
import ru.vtb.msa.noma.orchestrator.enums.Currency;
import ru.vtb.msa.noma.orchestrator.model.*;

import java.time.ZonedDateTime;
import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        imports = { ZonedDateTime.class, Currency.class }
)
public interface DtoMapper {

    /* ---------- TRANSACTION: entity → DTO ---------- */

    @Mapping(
            target = "timestamp",
            expression = "java(entity.getTimestamp().toLocalDate())"
    )
    TransactionDto transactionToDto(Transaction entity);

    List<TransactionDto> transactionToDtos(List<Transaction> entities);

    /* ---------- USER: DTO → entity и entity → DTO ---------- */

    // DTO → entity
    @Mapping(target = "id",               ignore = true)
    @Mapping(
            target = "registrationDate",
            expression = "java(ZonedDateTime.now())"
    )
    @Mapping(target = "version",          ignore = true)
    User toUser(UserDto dto);

    // entity → DTO
    UserDto userToDto(User user);

    /* ---------- ACCOUNT: DTO → entity ---------- */

    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "user",      source = "user")
    @Mapping(target = "balance",   source = "request.balance")
    @Mapping(
            target     = "currency",
            expression = "java(Currency.valueOf(request.currency()))"
    )
    @Mapping(target = "status",    constant = "ACTIVE")
    @Mapping(
            target     = "createdAt",
            expression = "java(ZonedDateTime.now())"
    )
    @Mapping(target = "version",   ignore = true)
    Account toAccount(CreateAccountRequest request, User user);

    /* ---------- ACCOUNT: entity → DTO ---------- */

    @Mapping(target = "user",     source = "account.user")
    @Mapping(
            target     = "currency",
            expression = "java(account.getCurrency().name())"
    )
    @Mapping(
            target     = "status",
            expression = "java(account.getStatus().name())"
    )
    @Mapping(target = "createdAt", source = "account.createdAt")
    AccountDto accountToDto(Account account);

    List<AccountDto> accountToDtos(List<Account> accounts);
}
