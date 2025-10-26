package ru.vtb.msa.noma.orchestrator.model;

import java.util.List;

public record BanksAndTypesResponseDto(
        List<BicBankDto> banks,
        List<AccountTransferTypeDto> types
) {
}
