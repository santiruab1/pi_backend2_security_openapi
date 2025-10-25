package com.example.pib2.controllers;

import com.example.pib2.models.dtos.LoginRequestDTO;
import com.example.pib2.models.dtos.LoginResponseDTO;
import com.example.pib2.models.entities.User;
import com.example.pib2.servicios.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para manejo de autenticación.
 * 
 * Este controlador proporciona endpoints para:
 * - Login de usuarios
 * - Generación de tokens JWT
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para autenticación de usuarios")
@CrossOrigin(origins = { "http://localhost:3000", "http://127.0.0.1:3000" })
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    /**
     * Endpoint para autenticación de usuarios.
     * 
     * @param loginRequest Datos de login (identification y password)
     * @return Respuesta con token JWT y datos del usuario
     */
    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario", description = "Autentica un usuario con su identificación y contraseña, devuelve un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            // Autenticar al usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getIdentification(),
                            loginRequest.getPassword()));

            // Obtener los detalles del usuario autenticado
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = (User) userDetails;

            // Generar token JWT
            String token = jwtService.generateToken(userDetails);

            // Crear respuesta con datos del usuario y token
            LoginResponseDTO response = new LoginResponseDTO(
                    token,
                    user.getId(),
                    user.getIdentification(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole(),
                    user.isEnabled());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * Endpoint para verificar el estado de autenticación.
     * 
     * @param authentication Información de autenticación actual
     * @return Información del usuario autenticado
     */
    @GetMapping("/me")
    @Operation(summary = "Obtener información del usuario autenticado", description = "Devuelve la información del usuario actualmente autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información del usuario obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario no autenticado");
        }

        User user = (User) authentication.getPrincipal();

        LoginResponseDTO response = new LoginResponseDTO(
                null, // No incluimos el token en esta respuesta
                user.getId(),
                user.getIdentification(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.isEnabled());

        return ResponseEntity.ok(response);
    }
}
