package ru.vtb.msa.noma.orchestrator.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.vtb.msa.noma.orchestrator.db.entity.Transaction;
import ru.vtb.msa.noma.orchestrator.model.TransactionDto;
import ru.vtb.msa.noma.orchestrator.model.TransactionRequest;

/**
 * MapStruct-маршрутизатор для конвертации между JPA-entity и DTO.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface DtoMapper {

    /**
     * Entity → DTO (для выдачи клиенту).
     * Преобразует LocalDateTime → LocalDate через вызов toLocalDate().
     */
    @Mapping(target = "id",               source = "entity.id")
    @Mapping(target = "senderAccountId", source = "entity.senderAccountId")
    @Mapping(target = "receiverAccountId", source = "entity.receiverAccountId")
    @Mapping(target = "amount",           source = "entity.amount")
    @Mapping(target = "currency",         source = "entity.currency")
    @Mapping(target = "description",      source = "entity.description")
    @Mapping(target = "timestamp",        expression = "java(entity.getTimestamp().toLocalDate())")
    TransactionDto fromEntityToDto(Transaction entity);

    /**
     * DTO → Entity (для записи в БД).
     * Поля id, status, timestamp и version игнорируются.
     */
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "status",    ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "version",   ignore = true)
    Transaction fromDtoToEntity(TransactionRequest request);
}