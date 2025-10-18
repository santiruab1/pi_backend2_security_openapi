package com.example.pib2.models.dtos.siigo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

//GET
@Data
public class PaymentMethodResponseDto {
    private Long id;
    private String name;
    
    @JsonProperty("payment_type")
    private String paymentType; 
    
}