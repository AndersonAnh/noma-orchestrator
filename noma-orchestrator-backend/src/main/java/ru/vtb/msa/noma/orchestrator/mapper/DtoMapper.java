package ru.vtb.msa.noma.orchestrator.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import ru.vtb.msa.noma.orchestrator.db.entity.Account;
import ru.vtb.msa.noma.orchestrator.db.entity.Transaction;
import ru.vtb.msa.noma.orchestrator.db.entity.User;
import ru.vtb.msa.noma.orchestrator.enums.Currency;
import ru.vtb.msa.noma.orchestrator.model.AccountDto;
import ru.vtb.msa.noma.orchestrator.model.CreateAccountRequest;
import ru.vtb.msa.noma.orchestrator.model.TransactionDto;
import ru.vtb.msa.noma.orchestrator.model.UserDto;

import java.time.ZonedDateTime;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        imports = {ZonedDateTime.class, Currency.class}
)
public interface DtoMapper {

    /* ---------- TRANSACTION: entity → DTO ---------- */

    @Mapping(target = "timestamp", expression = "java(entity.getTimestamp().toLocalDate())")
    TransactionDto transactionToDto(Transaction entity);

    /* ---------- USER: DTO → entity и entity → DTO ---------- */

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
    @Mapping(target = "currency", expression = "java(Currency.valueOf(request.currency().toUpperCase()))")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", expression = "java(ZonedDateTime.now())")
    @Mapping(target = "version", ignore = true)
    Account toAccount(CreateAccountRequest request, User user);

    /* ---------- ACCOUNT: entity → DTO ---------- */

    @Mapping(target = "user", source = "account.user")
    @Mapping(target = "currency", expression = "java(account.getCurrency().name())")
    @Mapping(target = "status", expression = "java(account.getStatus().name())")
    @Mapping(target = "createdAt", source = "account.createdAt")
    AccountDto accountToDto(Account account);
}
