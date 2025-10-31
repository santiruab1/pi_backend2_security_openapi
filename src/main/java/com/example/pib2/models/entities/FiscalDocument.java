package com.example.pib2.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "fiscal_documents")
public class FiscalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fiscal_document_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "cufe_cude", length = 500)
    private String cufeCude;

    @Column(name = "folio")
    private String folio;

    @Column(name = "prefix")
    private String prefix;

    @Column(name = "currency")
    private String currency;

    @Column(name = "payment_form")
    private String paymentForm;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "reception_date")
    private LocalDate receptionDate;

    @Column(name = "issuer_nit")
    private String issuerNit;

    @Column(name = "issuer_name", length = 500)
    private String issuerName;

    @Column(name = "receiver_nit")
    private String receiverNit;

    @Column(name = "receiver_name", length = 500)
    private String receiverName;

    @Column(name = "iva", precision = 19, scale = 2)
    private BigDecimal iva;

    @Column(name = "ica", precision = 19, scale = 2)
    private BigDecimal ica;

    @Column(name = "ic", precision = 19, scale = 2)
    private BigDecimal ic;

    @Column(name = "inc", precision = 19, scale = 2)
    private BigDecimal inc;

    @Column(name = "timbre", precision = 19, scale = 2)
    private BigDecimal timbre;

    @Column(name = "inc_bags", precision = 19, scale = 2)
    private BigDecimal incBags;

    @Column(name = "in_carbon", precision = 19, scale = 2)
    private BigDecimal inCarbon;

    @Column(name = "in_fuels", precision = 19, scale = 2)
    private BigDecimal inFuels;

    @Column(name = "ic_data", precision = 19, scale = 2)
    private BigDecimal icData;

    @Column(name = "icl", precision = 19, scale = 2)
    private BigDecimal icl;

    @Column(name = "inpp", precision = 19, scale = 2)
    private BigDecimal inpp;

    @Column(name = "ibua", precision = 19, scale = 2)
    private BigDecimal ibua;

    @Column(name = "icui", precision = 19, scale = 2)
    private BigDecimal icui;

    @Column(name = "rete_iva", precision = 19, scale = 2)
    private BigDecimal reteIva;

    @Column(name = "rete_rent", precision = 19, scale = 2)
    private BigDecimal reteRent;

    @Column(name = "rete_ica", precision = 19, scale = 2)
    private BigDecimal reteIca;

    @Column(name = "total", precision = 19, scale = 2)
    private BigDecimal total;

    @Column(name = "status")
    private String status;

    @Column(name = "group_info", length = 1000)
    private String groupInfo;
}

