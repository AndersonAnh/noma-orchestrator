package ru.vtb.msa.noma.orchestrator.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @Pattern(
            regexp = "^\\d{10}$|^\\d{12}$", // ИНН может быть 10 или 12 цифр
            message = "Invalid tax ID format"
    )
    @Column(name = "tax_id", unique = true)
    private String taxId;

    private String phone;
    private String email;

    @Column(name = "registration_date")
    private ZonedDateTime registrationDate;

    @Version
    private Integer version;
}
