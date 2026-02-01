package ru.vtb.msa.noma.orchestrator.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vtb.msa.noma.orchestrator.db.entity.InsuranceLife;

import java.util.UUID;

public interface InsuranceLifeRepository extends JpaRepository<InsuranceLife, UUID> {
}
