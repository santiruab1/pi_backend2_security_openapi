package com.example.pib2.models.dtos.siigo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SalesInvoiceRequestDto {
    
    @JsonProperty("document")
    private DocumentTypeDto document;
    
    @JsonProperty("date")
    private LocalDate date;
    
    @JsonProperty("customer")
    private CustomerDto customer; // Información del cliente
    
    @JsonProperty("items")
    private List<InvoiceItemDto> items;
    
    @JsonProperty("payments")
    private List<InvoicePaymentDto> payments;
    
}