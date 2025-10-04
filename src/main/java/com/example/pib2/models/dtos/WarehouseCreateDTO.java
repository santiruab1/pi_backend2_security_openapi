package com.example.pib2.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WarehouseCreateDTO {
    
    @NotBlank(message = "El nobre del almacén es obligatorio")
    @Size(max = 100, message = "El nombre del almacén no puede exceder los 100 caracteres")
    private String name;
}
