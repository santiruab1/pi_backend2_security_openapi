package com.example.pib2.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "sales_invoices")
@Data
public class SalesInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID local de tu base de datos

    @Column(unique = true)
    private String siigoId; // ID que retorna Siigo (GUID)

    @Column(nullable = false)
    private String documentNumber; // Número de factura de Siigo

    @Column(nullable = false)
    private LocalDate invoiceDate;

    private Double totalAmount;
    
    // Relación con la entidad User o Company si es necesario
    // @ManyToOne
    // @JoinColumn(name = "user_id")
    // private User user;
}