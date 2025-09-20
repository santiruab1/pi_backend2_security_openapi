package com.example.pib2.models.dtos;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la entidad CostCenter.
 * 
 * Se utiliza para transferir datos entre las capas de la aplicación
 * sin exponer la estructura interna de la entidad.
 */
@Data
public class CostCenterDTO {
    private Long id;
    private String code;
    private String name;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
