package com.example.pib2.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) para la entidad ThirdParty.
 * 
 * Se utiliza para transferir datos entre el cliente y el servidor,
 * proporcionando una capa de abstracción y validación.
 * 
 * Reglas de validación:
 * - Si businessName está presente, name y surname son opcionales
 * - Si businessName está vacío, name y surname son obligatorios
 * - Al menos uno de isSupplier o isClient debe ser true
 * - documentType es un número que identifica el tipo de documento
 * - documentNumber es el número de identificación como string
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThirdPartyDTO {
    
    private Long id;
    
    @Size(max = 255, message = "El nombre comercial no puede exceder 255 caracteres")
    private String businessName;
    
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;
    
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    private String surname;
    
    @NotNull(message = "El tipo de documento es obligatorio")
    private Integer documentType;
    
    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 50, message = "El número de documento no puede exceder 50 caracteres")
    private String documentNumber;
    
    @NotNull(message = "Debe especificar si es proveedor")
    private Boolean isSupplier;
    
    @NotNull(message = "Debe especificar si es cliente")
    private Boolean isClient;
    
    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
    private String address;
    
    @NotBlank(message = "El número de teléfono es obligatorio")
    @Size(max = 20, message = "El número de teléfono no puede exceder 20 caracteres")
    private String phoneNumber;
    
    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String city;
    
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    @Size(max = 255, message = "El correo electrónico no puede exceder 255 caracteres")
    private String mail;
    
    /**
     * Valida que se proporcione al menos un nombre comercial o nombre y apellido.
     * Esta validación se ejecuta a nivel de DTO.
     */
    @jakarta.validation.constraints.AssertTrue(message = "Debe proporcionar un nombre comercial o nombre y apellido")
    public boolean isValidNameConfiguration() {
        boolean hasBusinessName = businessName != null && !businessName.trim().isEmpty();
        boolean hasPersonalName = (name != null && !name.trim().isEmpty()) && 
                                 (surname != null && !surname.trim().isEmpty());
        
        return hasBusinessName || hasPersonalName;
    }
    
    /**
     * Valida que al menos uno de isSupplier o isClient sea true.
     * Esta validación se ejecuta a nivel de DTO.
     */
    @jakarta.validation.constraints.AssertTrue(message = "Debe ser al menos proveedor o cliente")
    public boolean isValidSupplierClientConfiguration() {
        return (isSupplier != null && isSupplier) || (isClient != null && isClient);
    }
}
