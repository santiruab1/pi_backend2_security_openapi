package com.example.pib2.models.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    
    private Long id;
    private String productName;
    private Integer quantity;
    private Long warehouseId;
    private LocalDateTime createdAt;
}