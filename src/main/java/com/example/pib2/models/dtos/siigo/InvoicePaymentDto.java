package com.example.pib2.models.dtos.siigo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InvoicePaymentDto {
    
    @JsonProperty("id")
    private Long paymentMethodId; 
    
    @JsonProperty("value")
    private Double paymentValue; 
    
    @JsonProperty("payment_type")
    private String paymentType; 
}