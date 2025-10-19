package com.example.pib2.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de login.
 * Contiene la información del usuario autenticado y el token de acceso.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String identification;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private boolean enabled;

    /**
     * Constructor para crear la respuesta de login con los datos del usuario.
     * 
     * @param token          Token de autenticación
     * @param id             ID del usuario
     * @param identification Identificación del usuario
     * @param email          Email del usuario
     * @param firstName      Nombre del usuario
     * @param lastName       Apellido del usuario
     * @param role           Rol del usuario
     * @param enabled        Estado de habilitación del usuario
     */
    public LoginResponseDTO(String token, Long id, String identification, String email,
            String firstName, String lastName, String role, boolean enabled) {
        this.token = token;
        this.id = id;
        this.identification = identification;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.enabled = enabled;
    }
}
