package com.example.pib2.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "document_type_id", nullable = false)
    private int documentTypeId;

    @Column(name = "document_date", nullable = false)
    private LocalDate documentDate;

    @Column(name = "document_reception", nullable = false)
    private LocalDate documentReception;

    @Column(name = "document_prefix", nullable = false)
    private String documentPrefix;

    @Column(name = "document_number", nullable = false)
    private String documentNumber;

    @Column(name = "document_due_date", nullable = false)
    private LocalDate documentDueDate;

    @Column(name = "third_party_id", nullable = false)
    private Long thirdPartyId;
}
