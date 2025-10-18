package com.example.pib2.controllers;

import com.example.pib2.models.dtos.UserDTO;
import com.example.pib2.models.entities.User;
import com.example.pib2.servicios.UserService;

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
 * Controlador REST para la gestión de usuarios.
 * Proporciona endpoints para operaciones CRUD sobre usuarios.
 * Requiere autenticación y rol ADMIN para todos los endpoints.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "API para gestión de usuarios del sistema")
@SecurityRequirement(name = "basicAuth")

public class UserController {
    @Autowired
    private UserService userService;

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setIdentification(user.getIdentification());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        return dto;
    }

    private User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setIdentification(dto.getIdentification());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        return user;
    }



    @GetMapping
    @Operation(
        summary = "Obtener todos los usuarios",
        description = "Retorna una lista de todos los usuarios registrados en el sistema. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de usuarios obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDTO.class)
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
    public List<UserDTO> getAll() {
        return userService.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }



    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener usuario por ID",
        description = "Retorna un usuario específico por su ID. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Usuario encontrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado"
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
    public ResponseEntity<UserDTO> getById(
        @Parameter(description = "ID del usuario a buscar", required = true)
        @PathVariable Long id
    ) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }



    @PostMapping
    @Operation(
        summary = "Crear nuevo usuario",
        description = "Crea un nuevo usuario en el sistema. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Usuario creado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDTO.class)
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
    public UserDTO create(
        @Parameter(description = "Datos del usuario a crear", required = true)
        @RequestBody UserDTO userDTO
    ) {
        User user = toEntity(userDTO);
        return toDTO(userService.save(user));
    }



    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar usuario",
        description = "Actualiza un usuario existente por su ID. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Usuario actualizado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado"
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
    public ResponseEntity<UserDTO> update(
        @Parameter(description = "ID del usuario a actualizar", required = true)
        @PathVariable Long id,
        @Parameter(description = "Nuevos datos del usuario", required = true)
        @RequestBody UserDTO userDTO
    ) {
        return userService.findById(id)
                .map(existing -> {
                    userDTO.setId(id);
                    User updated = toEntity(userDTO);
                    return ResponseEntity.ok(toDTO(userService.save(updated)));
                })
                .orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar usuario",
        description = "Elimina un usuario del sistema por su ID. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Usuario eliminado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuario no encontrado"
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
        @Parameter(description = "ID del usuario a eliminar", required = true)
        @PathVariable Long id
    ) {
        if (userService.findById(id).isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
