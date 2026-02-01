package ru.vtb.msa.noma.orchestrator.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MetricName {
    ALL_ACCOUNTS("all_accounts"),
    DELETE_ACCOUNT("delete_account"),
    UPDATE_ACCOUNT("update_account"),
    ACCOUNT_UPDATED_EVENT("account_updated_event"),
    CREATE_ACCOUNT("create_account"),
    TRANSACTION_PROCESS("transaction_process"),
    GET_TRANSACTION_BY_DATE("get_transaction_by_date"),
    GET_ACCOUNT_BY_ID("get_account_by_id"),
    GET_BANKS_AND_TYPES("get_banks_and_types"),
    CREATE_LIFE_POLICY("create_life_policy");

    private final String name;
}