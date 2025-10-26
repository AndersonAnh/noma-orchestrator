package ru.vtb.msa.noma.orchestrator.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "bic")
public class BicEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "bic_catalog", nullable = false, columnDefinition = "xml")
    private String bicCatalog;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;
}
