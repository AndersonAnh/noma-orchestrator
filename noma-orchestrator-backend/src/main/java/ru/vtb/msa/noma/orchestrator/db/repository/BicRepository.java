package ru.vtb.msa.noma.orchestrator.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vtb.msa.noma.orchestrator.db.entity.BicEntity;

import java.util.Optional;

public interface BicRepository extends JpaRepository<BicEntity,Integer> {

    Optional<BicEntity> findTopByOrderByUploadedAtDesc();
}
