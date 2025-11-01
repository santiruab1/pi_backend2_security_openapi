package com.example.pib2.controllers;

import com.example.pib2.models.dtos.UserDTO;
import com.example.pib2.models.dtos.UserCreateDTO;
import com.example.pib2.models.entities.User;
import com.example.pib2.servicios.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
 * El endpoint POST (crear usuario) es público, el resto requiere autenticación
 * y rol ADMIN.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "API para gestión de usuarios del sistema")
@SecurityRequirement(name = "basicAuth")

public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    private User toEntityFromCreateDTO(UserCreateDTO dto) {
        User user = new User();
        user.setIdentification(dto.getIdentification());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // Codificar password
        user.setRole(dto.getRole());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        return user;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista de todos los usuarios registrados en el sistema. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado - Credenciales requeridas"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN")
    })
    public List<UserDTO> getAll() {
        return userService.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Retorna un usuario específico por su ID. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Credenciales requeridas"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN")
    })
    public ResponseEntity<UserDTO> getById(
            @Parameter(description = "ID del usuario a buscar", required = true) @PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear nuevo usuario", description = "Crea un nuevo usuario en el sistema. Este endpoint es público y no requiere autenticación. El password será codificado automáticamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public UserDTO create(
            @Parameter(description = "Datos del usuario a crear incluyendo password y role", required = true) @RequestBody UserCreateDTO userCreateDTO) {
        User user = toEntityFromCreateDTO(userCreateDTO);
        return toDTO(userService.save(user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza un usuario existente por su ID. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Credenciales requeridas"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN")
    })
    public ResponseEntity<UserDTO> update(
            @Parameter(description = "ID del usuario a actualizar", required = true) @PathVariable Long id,
            @Parameter(description = "Nuevos datos del usuario", required = true) @RequestBody UserDTO userDTO) {
        return userService.findById(id)
                .map(existing -> {
                    userDTO.setId(id);
                    User updated = toEntity(userDTO);
                    return ResponseEntity.ok(toDTO(userService.save(updated)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema por su ID. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Credenciales requeridas"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del usuario a eliminar", required = true) @PathVariable Long id) {
        if (userService.findById(id).isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ==================== ENDPOINTS PARA GESTIÓN DE PERFIL PROPIO ====================
    
    @GetMapping("/me")
    @Operation(summary = "Obtener perfil del usuario actual", description = "Retorna la información del usuario autenticado actualmente. No requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil obtenido exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT requerido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UserDTO> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String identification = auth.getName();
        
        return userService.findByIdentification(identification)
                .map(user -> ResponseEntity.ok(toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    @Operation(summary = "Actualizar perfil propio", description = "Permite al usuario actualizar su propia información de perfil. No requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT requerido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<UserDTO> updateCurrentUser(
            @Parameter(description = "Nuevos datos del usuario", required = true) @RequestBody UserDTO userDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String identification = auth.getName();
        
        return userService.findByIdentification(identification)
                .map(existing -> {
                    // Actualizar solo los campos permitidos
                    existing.setIdentification(userDTO.getIdentification());
                    existing.setEmail(userDTO.getEmail());
                    existing.setFirstName(userDTO.getFirstName());
                    existing.setLastName(userDTO.getLastName());
                    
                    User updated = userService.save(existing);
                    return ResponseEntity.ok(toDTO(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/me")
    @Operation(summary = "Eliminar cuenta propia", description = "Permite al usuario eliminar su propia cuenta del sistema. No requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cuenta eliminada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado - Token JWT requerido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> deleteCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String identification = auth.getName();
        
        return userService.findByIdentification(identification)
                .map(user -> {
                    userService.deleteById(user.getId());
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
