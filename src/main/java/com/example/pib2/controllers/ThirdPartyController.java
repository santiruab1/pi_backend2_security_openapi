package com.example.pib2.controllers;

import com.example.pib2.models.dtos.ThirdPartyDTO;
import com.example.pib2.models.entities.ThirdParty;
import com.example.pib2.servicios.ThirdPartyService;

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

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de terceros.
 * Proporciona endpoints para operaciones CRUD sobre terceros.
 * Requiere autenticación y rol ADMIN para todos los endpoints.
 */
@RestController
@RequestMapping("/api/third-party")
@Tag(name = "Terceros", description = "API para gestión de terceros del sistema")
@SecurityRequirement(name = "basicAuth")
public class ThirdPartyController {
    
    @Autowired
    private ThirdPartyService thirdPartyService;
    
    /**
     * Convierte una entidad ThirdParty a DTO.
     */
    private ThirdPartyDTO toDTO(ThirdParty thirdParty) {
        ThirdPartyDTO dto = new ThirdPartyDTO();
        dto.setId(thirdParty.getId());
        dto.setBusinessName(thirdParty.getBusinessName());
        dto.setName(thirdParty.getName());
        dto.setSurname(thirdParty.getSurname());
        dto.setDocumentType(thirdParty.getDocumentType());
        dto.setDocumentNumber(thirdParty.getDocumentNumber());
        dto.setIsSupplier(thirdParty.getIsSupplier());
        dto.setIsClient(thirdParty.getIsClient());
        dto.setAddress(thirdParty.getAddress());
        dto.setPhoneNumber(thirdParty.getPhoneNumber());
        dto.setCity(thirdParty.getCity());
        dto.setMail(thirdParty.getMail());
        return dto;
    }
    
    /**
     * Convierte un DTO ThirdPartyDTO a entidad.
     */
    private ThirdParty toEntity(ThirdPartyDTO dto) {
        ThirdParty thirdParty = new ThirdParty();
        thirdParty.setId(dto.getId());
        thirdParty.setBusinessName(dto.getBusinessName());
        thirdParty.setName(dto.getName());
        thirdParty.setSurname(dto.getSurname());
        thirdParty.setDocumentType(dto.getDocumentType());
        thirdParty.setDocumentNumber(dto.getDocumentNumber());
        thirdParty.setIsSupplier(dto.getIsSupplier());
        thirdParty.setIsClient(dto.getIsClient());
        thirdParty.setAddress(dto.getAddress());
        thirdParty.setPhoneNumber(dto.getPhoneNumber());
        thirdParty.setCity(dto.getCity());
        thirdParty.setMail(dto.getMail());
        return thirdParty;
    }
    
    @GetMapping
    @Operation(
        summary = "Obtener todos los terceros",
        description = "Retorna una lista de todos los terceros registrados en el sistema. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de terceros obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ThirdPartyDTO.class)
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
    public List<ThirdPartyDTO> getAll() {
        return thirdPartyService.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener tercero por ID",
        description = "Retorna un tercero específico por su ID. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Tercero encontrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ThirdPartyDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Tercero no encontrado"
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
    public ResponseEntity<ThirdPartyDTO> getById(
            @Parameter(description = "ID del tercero") 
            @PathVariable Long id) {
        return thirdPartyService.findById(id)
                .map(thirdParty -> ResponseEntity.ok(toDTO(thirdParty)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(
        summary = "Crear nuevo tercero",
        description = "Crea un nuevo tercero en el sistema. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Tercero creado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ThirdPartyDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de entrada inválidos"
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
    public ResponseEntity<ThirdPartyDTO> create(@Valid @RequestBody ThirdPartyDTO thirdPartyDTO) {
        // Verificar si ya existe un tercero con el mismo correo
        if (thirdPartyService.existsByMail(thirdPartyDTO.getMail())) {
            return ResponseEntity.badRequest().build();
        }
        
        ThirdParty thirdParty = toEntity(thirdPartyDTO);
        ThirdParty savedThirdParty = thirdPartyService.save(thirdParty);
        return ResponseEntity.ok(toDTO(savedThirdParty));
    }
    
    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar tercero",
        description = "Actualiza un tercero existente por su ID. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Tercero actualizado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ThirdPartyDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de entrada inválidos"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Tercero no encontrado"
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
    public ResponseEntity<ThirdPartyDTO> update(
            @Parameter(description = "ID del tercero") 
            @PathVariable Long id, 
            @Valid @RequestBody ThirdPartyDTO thirdPartyDTO) {
        return thirdPartyService.findById(id)
                .map(existing -> {
                    thirdPartyDTO.setId(id);
                    ThirdParty updated = toEntity(thirdPartyDTO);
                    return ResponseEntity.ok(toDTO(thirdPartyService.save(updated)));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar tercero",
        description = "Elimina un tercero por su ID. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Tercero eliminado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Tercero no encontrado"
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
            @Parameter(description = "ID del tercero") 
            @PathVariable Long id) {
        if (thirdPartyService.findById(id).isPresent()) {
            thirdPartyService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/search/by-supplier")
    @Operation(
        summary = "Buscar terceros proveedores",
        description = "Busca terceros que son proveedores. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de terceros proveedores encontrados",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ThirdPartyDTO.class)
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
    public List<ThirdPartyDTO> getBySupplier() {
        return thirdPartyService.findByIsSupplier(true).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/search/by-client")
    @Operation(
        summary = "Buscar terceros clientes",
        description = "Busca terceros que son clientes. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de terceros clientes encontrados",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ThirdPartyDTO.class)
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
    public List<ThirdPartyDTO> getByClient() {
        return thirdPartyService.findByIsClient(true).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/search/by-document-type")
    @Operation(
        summary = "Buscar terceros por tipo de documento",
        description = "Busca terceros por tipo de documento (número). Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de terceros encontrados",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ThirdPartyDTO.class)
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
    public List<ThirdPartyDTO> getByDocumentType(
            @Parameter(description = "Tipo de documento (número)") 
            @RequestParam Integer documentType) {
        return thirdPartyService.findByDocumentType(documentType).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/search/by-city")
    @Operation(
        summary = "Buscar terceros por ciudad",
        description = "Busca terceros por ciudad. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de terceros encontrados",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ThirdPartyDTO.class)
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
    public List<ThirdPartyDTO> getByCity(
            @Parameter(description = "Ciudad") 
            @RequestParam String city) {
        return thirdPartyService.findByCity(city).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
