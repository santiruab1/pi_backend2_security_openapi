package com.example.pib2.controllers;

import com.example.pib2.models.dtos.CostCenterDTO;
import com.example.pib2.models.entities.CostCenter;
import com.example.pib2.servicios.CostCenterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de centros de costo.
 * 
 * Proporciona endpoints para operaciones CRUD sobre centros de costo.
 * Requiere autenticación y rol ADMIN para todos los endpoints.
 */
@RestController
@RequestMapping("/api/cost-centers")
@Tag(name = "Centros de Costo", description = "API para gestión de centros de costo del sistema")
@SecurityRequirement(name = "basicAuth")
public class CostCenterController {
    
    @Autowired
    private CostCenterService costCenterService;

    /**
     * Convierte una entidad CostCenter a DTO.
     * 
     * @param costCenter La entidad CostCenter
     * @return CostCenterDTO El DTO correspondiente
     */
    private CostCenterDTO toDTO(CostCenter costCenter) {
        CostCenterDTO dto = new CostCenterDTO();
        dto.setId(costCenter.getId());
        dto.setCode(costCenter.getCode());
        dto.setName(costCenter.getName());
        dto.setActive(costCenter.getActive());
        dto.setCreatedAt(costCenter.getCreatedAt());
        dto.setUpdatedAt(costCenter.getUpdatedAt());
        dto.setCreatedBy(costCenter.getCreatedBy());
        dto.setUpdatedBy(costCenter.getUpdatedBy());
        return dto;
    }

    /**
     * Convierte un DTO a entidad CostCenter.
     * 
     * @param dto El DTO CostCenterDTO
     * @return CostCenter La entidad correspondiente
     */
    private CostCenter toEntity(CostCenterDTO dto) {
        CostCenter costCenter = new CostCenter();
        costCenter.setId(dto.getId());
        costCenter.setCode(dto.getCode());
        costCenter.setName(dto.getName());
        costCenter.setActive(dto.getActive());
        return costCenter;
    }

    @GetMapping
    @Operation(
        summary = "Obtener todos los centros de costo",
        description = "Retorna una lista de todos los centros de costo registrados en el sistema. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de centros de costo obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CostCenterDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autenticado - Credenciales requeridas"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Acceso denegado - Requiere rol ADMIN"
        )
    })
    public List<CostCenterDTO> getAll() {
        return costCenterService.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/active")
    @Operation(
        summary = "Obtener centros de costo activos",
        description = "Retorna una lista de todos los centros de costo activos. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de centros de costo activos obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CostCenterDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autenticado - Credenciales requeridas"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Acceso denegado - Requiere rol ADMIN"
        )
    })
    public List<CostCenterDTO> getActive() {
        return costCenterService.findActive().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    @Operation(
        summary = "Buscar centros de costo por nombre",
        description = "Busca centros de costo que contengan el nombre especificado. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Búsqueda realizada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CostCenterDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autenticado - Credenciales requeridas"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Acceso denegado - Requiere rol ADMIN"
        )
    })
    public List<CostCenterDTO> searchByName(
        @Parameter(description = "Nombre o parte del nombre a buscar", required = true)
        @RequestParam String name
    ) {
        return costCenterService.findByNameContaining(name).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener centro de costo por ID",
        description = "Retorna un centro de costo específico por su ID. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Centro de costo encontrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CostCenterDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Centro de costo no encontrado"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autenticado - Credenciales requeridas"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Acceso denegado - Requiere rol ADMIN"
        )
    })
    public ResponseEntity<CostCenterDTO> getById(
        @Parameter(description = "ID del centro de costo a buscar", required = true)
        @PathVariable Long id
    ) {
        return costCenterService.findById(id)
                .map(costCenter -> ResponseEntity.ok(toDTO(costCenter)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    @Operation(
        summary = "Obtener centro de costo por código",
        description = "Retorna un centro de costo específico por su código. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Centro de costo encontrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CostCenterDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Centro de costo no encontrado"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autenticado - Credenciales requeridas"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Acceso denegado - Requiere rol ADMIN"
        )
    })
    public ResponseEntity<CostCenterDTO> getByCode(
        @Parameter(description = "Código del centro de costo a buscar", required = true)
        @PathVariable String code
    ) {
        return costCenterService.findByCode(code)
                .map(costCenter -> ResponseEntity.ok(toDTO(costCenter)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
        summary = "Crear nuevo centro de costo",
        description = "Crea un nuevo centro de costo en el sistema. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Centro de costo creado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CostCenterDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de entrada inválidos o código duplicado"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autenticado - Credenciales requeridas"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Acceso denegado - Requiere rol ADMIN"
        )
    })
    public ResponseEntity<CostCenterDTO> create(
        @Parameter(description = "Datos del centro de costo a crear", required = true)
        @RequestBody CostCenterDTO costCenterDTO
    ) {
        try {
            CostCenter costCenter = toEntity(costCenterDTO);
            CostCenter saved = costCenterService.save(costCenter);
            return ResponseEntity.ok(toDTO(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar centro de costo",
        description = "Actualiza un centro de costo existente por su ID. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Centro de costo actualizado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CostCenterDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Centro de costo no encontrado"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de entrada inválidos o código duplicado"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autenticado - Credenciales requeridas"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Acceso denegado - Requiere rol ADMIN"
        )
    })
    public ResponseEntity<CostCenterDTO> update(
        @Parameter(description = "ID del centro de costo a actualizar", required = true)
        @PathVariable Long id,
        @Parameter(description = "Nuevos datos del centro de costo", required = true)
        @RequestBody CostCenterDTO costCenterDTO
    ) {
        try {
            return costCenterService.update(id, toEntity(costCenterDTO))
                    .map(costCenter -> ResponseEntity.ok(toDTO(costCenter)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Cambiar estado del centro de costo",
        description = "Activa o desactiva un centro de costo. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Estado del centro de costo actualizado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CostCenterDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Centro de costo no encontrado"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autenticado - Credenciales requeridas"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Acceso denegado - Requiere rol ADMIN"
        )
    })
    public ResponseEntity<CostCenterDTO> setStatus(
        @Parameter(description = "ID del centro de costo", required = true)
        @PathVariable Long id,
        @Parameter(description = "Nuevo estado activo/inactivo", required = true)
        @RequestParam boolean active
    ) {
        return costCenterService.setActive(id, active)
                .map(costCenter -> ResponseEntity.ok(toDTO(costCenter)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar centro de costo",
        description = "Elimina un centro de costo del sistema por su ID. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Centro de costo eliminado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Centro de costo no encontrado"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autenticado - Credenciales requeridas"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Acceso denegado - Requiere rol ADMIN"
        )
    })
    public ResponseEntity<Void> delete(
        @Parameter(description = "ID del centro de costo a eliminar", required = true)
        @PathVariable Long id
    ) {
        if (costCenterService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
