package ru.vtb.msa.noma.orchestrator.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vtb.msa.noma.orchestrator.db.entity.Account;
import ru.vtb.msa.noma.orchestrator.db.entity.Transaction;
import ru.vtb.msa.noma.orchestrator.db.repository.AccountRepository;
import ru.vtb.msa.noma.orchestrator.db.repository.TransactionRepository;
import ru.vtb.msa.noma.orchestrator.enums.Currency;
import ru.vtb.msa.noma.orchestrator.exception.CurrencyMisMatchException;
import ru.vtb.msa.noma.orchestrator.exception.NotEnoughFundsException;
import ru.vtb.msa.noma.orchestrator.exception.TransactionReceiverNotFoundException;
import ru.vtb.msa.noma.orchestrator.exception.TransactionSenderNotFoundException;
import ru.vtb.msa.noma.orchestrator.mapper.DtoMapper;
import ru.vtb.msa.noma.orchestrator.model.TransactionRequest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private DtoMapper dtoMapper;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldExecuteTransaction() {

        TransactionRequest transactionRequest = TestTransactionUtil.getTransactionRequest();
        Account sender = TestTransactionUtil.getAccountSender();
        Account receiver = TestTransactionUtil.getAccountReceiver();


        Transaction expectedTransaction = Transaction.builder()
                .id(UUID.randomUUID())
                .build();

        when(accountRepository.findById(transactionRequest.senderAccountId()))
                .thenReturn(Optional.of(sender));
        when(accountRepository.findById(transactionRequest.receiverAccountId()))
                .thenReturn(Optional.of(receiver));
        when(accountRepository.save(sender))
                .thenReturn(sender);
        when(accountRepository.save(receiver))
                .thenReturn(receiver);
        when(dtoMapper.toTransaction(transactionRequest))
                .thenReturn(expectedTransaction);
        when(transactionRepository.save(expectedTransaction))
                .thenReturn(expectedTransaction);

        transactionService.executeTransaction(transactionRequest);

        double expectedSenderBalance = 1000.0 - 900.0;
        double expectedReceiverBalance = 100.0 + 900.0;

        Assertions.assertEquals(expectedSenderBalance, sender.getBalance());
        Assertions.assertEquals(expectedReceiverBalance, receiver.getBalance());

        verify(accountRepository).save(sender);
        verify(accountRepository).save(receiver);
    }

    @Test
    void shouldTransactionSenderNotFoundException() {
        TransactionRequest transactionRequest = TestTransactionUtil.getTransactionRequest();

        when(accountRepository.findById(transactionRequest.senderAccountId()))
                .thenReturn(Optional.empty());



        assertThrows(TransactionSenderNotFoundException.class,
                () -> transactionService.executeTransaction(transactionRequest));

        verifyNoInteractions(transactionRepository);

        verify(accountRepository, times(1)).findById(transactionRequest.senderAccountId());
        verify(accountRepository, never()).findById(transactionRequest.receiverAccountId());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldTransactionReceiverNotFoundException() {
        TransactionRequest transactionRequest = TestTransactionUtil.getTransactionRequest();
        Account sender = TestTransactionUtil.getAccountSender();

        when(accountRepository.findById(transactionRequest.senderAccountId()))
                .thenReturn(Optional.of(sender));
        when(accountRepository.findById(transactionRequest.receiverAccountId()))
                .thenReturn(Optional.empty());

        assertThrows(TransactionReceiverNotFoundException.class,
                () -> transactionService.executeTransaction(transactionRequest));

        verifyNoInteractions(transactionRepository);

        verify(accountRepository, times(1)).findById(transactionRequest.senderAccountId());
        verify(accountRepository, times(1)).findById(transactionRequest.receiverAccountId());
    }

    @Test
    void shouldThrowCurrencyMisMatchException() {
        TransactionRequest transactionRequest = TestTransactionUtil.getTransactionRequest();
        Account sender = TestTransactionUtil.getAccountSender();
        Account receiver = TestTransactionUtil.getAccountReceiver();

        receiver.setCurrency(Currency.USD);

        when(accountRepository.findById(transactionRequest.senderAccountId()))
                .thenReturn(Optional.of(sender));
        when(accountRepository.findById(transactionRequest.receiverAccountId()))
                .thenReturn(Optional.of(receiver));

        assertThrows(CurrencyMisMatchException.class,
                () -> transactionService.executeTransaction(transactionRequest));

        verifyNoInteractions(transactionRepository);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldThrowNotEnoughFundsException() {
        TransactionRequest transactionRequest = TestTransactionUtil.getTransactionRequest();
        Account sender = TestTransactionUtil.getAccountSender();
        Account receiver = TestTransactionUtil.getAccountReceiver();

        sender.setBalance(500.0);

        when(accountRepository.findById(transactionRequest.senderAccountId()))
                .thenReturn(Optional.of(sender));
        when(accountRepository.findById(transactionRequest.receiverAccountId()))
                .thenReturn(Optional.of(receiver));

        assertThrows(NotEnoughFundsException.class,
                () -> transactionService.executeTransaction(transactionRequest));

        verifyNoInteractions(transactionRepository);
        verify(accountRepository, never()).save(any(Account.class));
    }
}