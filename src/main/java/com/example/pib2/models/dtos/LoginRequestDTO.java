package com.example.pib2.models.dtos;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la solicitud de login.
 * Contiene las credenciales necesarias para autenticar un usuario.
 */
@Data
public class LoginRequestDTO {

    @NotBlank(message = "La identificación es obligatoria")
    private String identification;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
