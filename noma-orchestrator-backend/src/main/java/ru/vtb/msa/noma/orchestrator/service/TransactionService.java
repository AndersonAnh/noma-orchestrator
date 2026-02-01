package ru.vtb.msa.noma.orchestrator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vtb.msa.noma.orchestrator.db.entity.Account;
import ru.vtb.msa.noma.orchestrator.db.entity.Transaction;
import ru.vtb.msa.noma.orchestrator.db.repository.AccountRepository;
import ru.vtb.msa.noma.orchestrator.db.repository.TransactionRepository;
import ru.vtb.msa.noma.orchestrator.exception.CurrencyMisMatchException;
import ru.vtb.msa.noma.orchestrator.exception.NotEnoughFundsException;
import ru.vtb.msa.noma.orchestrator.exception.TransactionReceiverNotFoundException;
import ru.vtb.msa.noma.orchestrator.exception.TransactionSenderNotFoundException;
import ru.vtb.msa.noma.orchestrator.mapper.DtoMapper;
import ru.vtb.msa.noma.orchestrator.model.TransactionRequest;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    private final DtoMapper dtoMapper;

    @Transactional
    public void executeTransaction(TransactionRequest request) {

        Account sender = accountRepository.findById(request.senderAccountId()).orElseThrow(() -> new TransactionSenderNotFoundException("Отправитель не найден"));
        Account receiver = accountRepository.findById(request.receiverAccountId()).orElseThrow(() -> new TransactionReceiverNotFoundException("Получатель не найден"));

        if (!sender.getCurrency().equals(receiver.getCurrency())) {
            throw new CurrencyMisMatchException("Невалидная валюта для операции");
        }
        if (request.amount() > sender.getBalance()) {
            throw new NotEnoughFundsException("Недостаточно средств");
        }

        double senderBalance = sender.getBalance() - request.amount();
        double receiverBalance = receiver.getBalance() + request.amount();

        sender.setBalance(senderBalance);
        receiver.setBalance(receiverBalance);

        accountRepository.save(sender);
        accountRepository.save(receiver);

        Transaction transaction = dtoMapper.toTransaction(request);
        transactionRepository.save(transaction);
    }
}