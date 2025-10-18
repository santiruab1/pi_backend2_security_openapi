package com.example.pib2.repositories;

import com.example.pib2.models.entities.ThirdParty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad ThirdParty.
 * 
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas
 * sobre la tabla third_party en la base de datos.
 */
@Repository
public interface ThirdPartyRepository extends JpaRepository<ThirdParty, Long> {
    
    /**
     * Busca un tercero por su correo electrónico.
     * 
     * @param mail el correo electrónico del tercero
     * @return Optional que contiene el ThirdParty si existe, vacío en caso contrario
     */
    Optional<ThirdParty> findByMail(String mail);
    
    /**
     * Verifica si existe un tercero con el correo electrónico especificado.
     * 
     * @param mail el correo electrónico a verificar
     * @return true si existe un tercero con ese correo, false en caso contrario
     */
    boolean existsByMail(String mail);
    
    /**
     * Busca terceros por tipo de documento.
     * 
     * @param documentType el tipo de documento (número)
     * @return lista de terceros que coinciden con el tipo de documento
     */
    java.util.List<ThirdParty> findByDocumentType(Integer documentType);
    
    /**
     * Busca terceros por número de documento.
     * 
     * @param documentNumber el número de documento del tercero
     * @return Optional que contiene el ThirdParty si existe, vacío en caso contrario
     */
    Optional<ThirdParty> findByDocumentNumber(String documentNumber);
    
    /**
     * Verifica si existe un tercero con el número de documento especificado.
     * 
     * @param documentNumber el número de documento a verificar
     * @return true si existe un tercero con ese número de documento, false en caso contrario
     */
    boolean existsByDocumentNumber(String documentNumber);
    
    /**
     * Busca terceros que son proveedores.
     * 
     * @param isSupplier true para buscar proveedores
     * @return lista de terceros que son proveedores
     */
    java.util.List<ThirdParty> findByIsSupplier(Boolean isSupplier);
    
    /**
     * Busca terceros que son clientes.
     * 
     * @param isClient true para buscar clientes
     * @return lista de terceros que son clientes
     */
    java.util.List<ThirdParty> findByIsClient(Boolean isClient);
    
    /**
     * Busca terceros que son tanto proveedores como clientes.
     * 
     * @param isSupplier true para buscar proveedores
     * @param isClient true para buscar clientes
     * @return lista de terceros que cumplen ambas condiciones
     */
    java.util.List<ThirdParty> findByIsSupplierAndIsClient(Boolean isSupplier, Boolean isClient);
    
    /**
     * Busca terceros por ciudad.
     * 
     * @param city la ciudad donde se encuentra el tercero
     * @return lista de terceros que están en la ciudad especificada
     */
    java.util.List<ThirdParty> findByCity(String city);
    
    /**
     * Busca terceros por nombre comercial.
     * 
     * @param businessName el nombre comercial del tercero
     * @return lista de terceros que coinciden con el nombre comercial
     */
    java.util.List<ThirdParty> findByBusinessNameContainingIgnoreCase(String businessName);
    
    /**
     * Busca terceros por nombre y apellido.
     * 
     * @param name el nombre del tercero
     * @param surname el apellido del tercero
     * @return lista de terceros que coinciden con el nombre y apellido
     */
    java.util.List<ThirdParty> findByNameAndSurname(String name, String surname);
}
