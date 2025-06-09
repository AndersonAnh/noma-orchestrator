package ru.vtb.msa.noma.orchestrator.db.repositorty;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vtb.msa.noma.orchestrator.db.entity.Account;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
}
