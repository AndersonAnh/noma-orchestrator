package ru.vtb.msa.noma.orchestrator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vtb.msa.noma.orchestrator.db.entity.Account;
import ru.vtb.msa.noma.orchestrator.db.entity.Transaction;
import ru.vtb.msa.noma.orchestrator.db.repository.AccountRepository;
import ru.vtb.msa.noma.orchestrator.db.repository.TransactionRepository;
import ru.vtb.msa.noma.orchestrator.enums.TransactionStatus;
import ru.vtb.msa.noma.orchestrator.exception.CurrencyMisMatchException;
import ru.vtb.msa.noma.orchestrator.exception.NotEnoughFundsException;
import ru.vtb.msa.noma.orchestrator.exception.TransactionReceiverNotFoundException;
import ru.vtb.msa.noma.orchestrator.exception.TransactionSenderNotFoundException;
import ru.vtb.msa.noma.orchestrator.model.TransactionRequest;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    @Transactional
    public void executeTransaction(TransactionRequest request) {

        Account sender = accountRepository.findById(request.senderAccountId()).orElseThrow(() -> new TransactionSenderNotFoundException("Отправитель не найден"));
        Account receiver = accountRepository.findById(request.receiverAccountId()).orElseThrow(() -> new TransactionReceiverNotFoundException("Получатель не найден"));

        //если валюта отправителя и получателя не совпадают то операция не выполняется
        if (!sender.getCurrency().equals(receiver.getCurrency())) {
            throw new CurrencyMisMatchException("Невалидная валюта для операции");
        }

        //валидация баланса - баланс который приходит с фронта не дб больше баланса отправителя
        if (request.amount() > sender.getBalance()) {
            throw new NotEnoughFundsException("Недостаточно средств");
        }

        //актуализировать балансы-тк балансы изменились

        double senderBalance = sender.getBalance() - request.amount();
        double receiverBalance = receiver.getBalance() + request.amount();

        sender.setBalance(senderBalance);
        receiver.setBalance(receiverBalance);

        //сохранить в бд результаты отправителя и получателя с изменеными балансами

        accountRepository.save(sender);
        accountRepository.save(receiver);

        //сохрани транзакцию

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
