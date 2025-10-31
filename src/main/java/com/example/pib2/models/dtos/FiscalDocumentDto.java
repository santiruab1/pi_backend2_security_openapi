package com.example.pib2.models.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FiscalDocumentDto {
    private Long id;
    private String documentType;
    private String cufeCude;
    private String folio;
    private String prefix;
    private String currency;
    private String paymentForm;
    private String paymentMethod;
    private LocalDate issueDate;
    private LocalDate receptionDate;
    private String issuerNit;
    private String issuerName;
    private String receiverNit;
    private String receiverName;
    private BigDecimal iva;
    private BigDecimal ica;
    private BigDecimal ic;
    private BigDecimal inc;
    private BigDecimal timbre;
    private BigDecimal incBags;
    private BigDecimal inCarbon;
    private BigDecimal inFuels;
    private BigDecimal icData;
    private BigDecimal icl;
    private BigDecimal inpp;
    private BigDecimal ibua;
    private BigDecimal icui;
    private BigDecimal reteIva;
    private BigDecimal reteRent;
    private BigDecimal reteIca;
    private BigDecimal total;
    private String status;
    private String groupInfo;
}

