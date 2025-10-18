package com.example.pib2.models.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WarehouseDTO {
    
    private Long id;
    private String name;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
