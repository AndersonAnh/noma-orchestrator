package ru.vtb.msa.noma.orchestrator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vtb.msa.noma.orchestrator.db.entity.Account;
import ru.vtb.msa.noma.orchestrator.db.entity.Transaction;
import ru.vtb.msa.noma.orchestrator.db.repositorty.AccountRepository;
import ru.vtb.msa.noma.orchestrator.db.repositorty.TransactionRepository;
import ru.vtb.msa.noma.orchestrator.enums.TransactionStatus;
import ru.vtb.msa.noma.orchestrator.exception.NotEnoughFundsException;
import ru.vtb.msa.noma.orchestrator.model.TransactionRequest;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    @Transactional
    public void executeTransaction(TransactionRequest request, Account senderAccount,
                                   Account receiverAccount) {
        if (senderAccount.getBalance() < request.amount()) {
            throw new NotEnoughFundsException("Недостаточно средств");
        }
        double senderBalance = senderAccount.getBalance() - request.amount();
        double receiverBalance = receiverAccount.getBalance() + request.amount();
        senderAccount.setBalance(senderBalance);
        receiverAccount.setBalance(receiverBalance);
        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);
        Transaction transaction = createNewTransaction(request);
        transactionRepository.save(transaction);
    }

    private Transaction createNewTransaction(TransactionRequest request) {
        return Transaction.builder()
                .senderAccountId(request.senderAccountId())
                .receiverAccountId(request.receiverAccountId())
                .amount(request.amount())
                .currency(request.currency())
                .status(TransactionStatus.COMPLETED)
                .timestamp(LocalDateTime.now())
                .description(request.description())
                .build();
    }
}
