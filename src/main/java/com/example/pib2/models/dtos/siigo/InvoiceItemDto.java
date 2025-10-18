package com.example.pib2.models.dtos.siigo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class InvoiceItemDto {
    
    @JsonProperty("code")
    private String productCode;
    
    private Double quantity;
    private Double price;
    
    @JsonProperty("taxes")
    private List<TaxReferenceDto> taxes;
}