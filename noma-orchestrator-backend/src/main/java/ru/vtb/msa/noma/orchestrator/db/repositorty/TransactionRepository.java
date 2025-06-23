package ru.vtb.msa.noma.orchestrator.db.repositorty;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vtb.msa.noma.orchestrator.db.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
