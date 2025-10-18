package com.example.pib2.models.dtos.siigo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PriceDto {
    @JsonProperty("currency_code")
    private String currencyCode;
    @JsonProperty("price_list")
    private List<PriceListDto> priceList;
}