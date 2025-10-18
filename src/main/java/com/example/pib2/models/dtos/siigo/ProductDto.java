package com.example.pib2.models.dtos.siigo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProductDto {
    private String code;
    private String name;
    @JsonProperty("account_group")
    private Long accountGroup;
    private String type;
    @JsonProperty("stock_control")
    private Boolean stockControl;
    private Boolean active;
    @JsonProperty("tax_classification")
    private String taxClassification;
    @JsonProperty("tax_included")
    private Boolean taxIncluded;
    @JsonProperty("tax_consumption_value")
    private Double taxConsumptionValue;
    private List<TaxDto> taxes;
    private List<PriceDto> prices;
    private String unit;
    @JsonProperty("unit_label")
    private String unitLabel;
    private String reference;
    private String description;
    @JsonProperty("additional_fields")
    private AdditionalFieldsDto additionalFields;
}