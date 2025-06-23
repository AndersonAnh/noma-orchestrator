package ru.vtb.msa.noma.orchestrator.db.repositorty;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vtb.msa.noma.orchestrator.db.entity.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User,UUID> {
}
