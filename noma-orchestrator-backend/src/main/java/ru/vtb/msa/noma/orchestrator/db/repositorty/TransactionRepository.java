package ru.vtb.msa.noma.orchestrator.db.repositorty;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vtb.msa.noma.orchestrator.db.entity.Transaction;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
