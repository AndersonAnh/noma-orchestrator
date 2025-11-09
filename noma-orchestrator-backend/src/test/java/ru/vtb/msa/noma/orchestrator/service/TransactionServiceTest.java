package ru.vtb.msa.noma.orchestrator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vtb.msa.noma.orchestrator.db.entity.Account;
import ru.vtb.msa.noma.orchestrator.db.entity.Transaction;
import ru.vtb.msa.noma.orchestrator.db.repository.AccountRepository;
import ru.vtb.msa.noma.orchestrator.db.repository.TransactionRepository;
import ru.vtb.msa.noma.orchestrator.enums.TransactionStatus;
import ru.vtb.msa.noma.orchestrator.exception.NotEnoughFundsException;
import ru.vtb.msa.noma.orchestrator.model.TransactionRequest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    public void shouldExecuteTransactionSuccessfullyWhenFundsAreSufficient() {

        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        Account sender = new Account();
        sender.setId(senderId);
        sender.setBalance(100.0);

        Account receiver = new Account();
        receiver.setId(receiverId);
        receiver.setBalance(50.0);

        TransactionRequest request = new TransactionRequest(
                senderId,
                receiverId,
                30.0,
                "RUB",
                "Оплата счета №123"
        );


        transactionService.executeTransaction(request);


        assertEquals(70.0, sender.getBalance(), 1e-6);
        assertEquals(80.0, receiver.getBalance(), 1e-6);


        verify(accountRepository).save(sender);
        verify(accountRepository).save(receiver);


        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(txCaptor.capture());
        Transaction tx = txCaptor.getValue();

        assertEquals(senderId, tx.getSenderAccountId());
        assertEquals(receiverId, tx.getReceiverAccountId());
        assertEquals(request.amount(), tx.getAmount(), 1e-6);
        assertEquals(request.currency(), tx.getCurrency());
        assertEquals(TransactionStatus.COMPLETED, tx.getStatus());
        assertEquals(request.description(), tx.getDescription());
        assertNotNull(tx.getTimestamp());
        assertTrue(tx.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    public void shouldThrowNotEnoughFundsExceptionWhenFundsAreInsufficient() {

        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        Account sender = new Account();
        sender.setId(senderId);
        sender.setBalance(20.0);

        Account receiver = new Account();
        receiver.setId(receiverId);
        receiver.setBalance(50.0);

        TransactionRequest request = new TransactionRequest(
                senderId,
                receiverId,
                30.0,
                "RUB",
                "Попытка списания средств при недостатке баланса"
        );


        NotEnoughFundsException ex = assertThrows(
                NotEnoughFundsException.class,
                () -> transactionService.executeTransaction(request)
        );
        assertEquals("Недостаточно средств", ex.getMessage());


        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }
}
