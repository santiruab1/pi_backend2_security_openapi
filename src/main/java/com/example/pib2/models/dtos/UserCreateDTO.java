package com.example.pib2.models.dtos;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de usuarios.
 * Incluye campos requeridos para el registro como password y role.
 */
@Data
public class UserCreateDTO {

    @NotBlank(message = "La identificación es obligatoria")
    @Size(min = 5, max = 20, message = "La identificación debe tener entre 5 y 20 caracteres")
    private String identification;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String lastName;

    @NotBlank(message = "El rol es obligatorio")
    private String role = "USER"; // Valor por defecto
}
