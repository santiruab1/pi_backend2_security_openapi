package com.example.pib2.models.dtos;

import lombok.Data;

@Data
public class ItemDTO {
    private Long id;
    private String name;
    private String description;
    private int quantity;
}
