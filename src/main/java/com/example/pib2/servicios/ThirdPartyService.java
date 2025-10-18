package com.example.pib2.servicios;

import com.example.pib2.models.entities.ThirdParty;
import com.example.pib2.repositories.ThirdPartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de terceros.
 * 
 * Contiene la lógica de negocio para operaciones CRUD y consultas
 * relacionadas con la entidad ThirdParty.
 */
@Service
public class ThirdPartyService {
    
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    
    /**
     * Obtiene todos los terceros registrados.
     * 
     * @return lista de todos los terceros
     */
    public List<ThirdParty> findAll() {
        return thirdPartyRepository.findAll();
    }
    
    /**
     * Busca un tercero por su ID.
     * 
     * @param id el ID del tercero
     * @return Optional que contiene el ThirdParty si existe, vacío en caso contrario
     */
    public Optional<ThirdParty> findById(Long id) {
        return thirdPartyRepository.findById(id);
    }
    
    /**
     * Busca un tercero por su correo electrónico.
     * 
     * @param mail el correo electrónico del tercero
     * @return Optional que contiene el ThirdParty si existe, vacío en caso contrario
     */
    public Optional<ThirdParty> findByMail(String mail) {
        return thirdPartyRepository.findByMail(mail);
    }
    
    /**
     * Guarda un tercero (crear o actualizar).
     * 
     * @param thirdParty el tercero a guardar
     * @return el tercero guardado
     */
    public ThirdParty save(ThirdParty thirdParty) {
        return thirdPartyRepository.save(thirdParty);
    }
    
    /**
     * Elimina un tercero por su ID.
     * 
     * @param id el ID del tercero a eliminar
     */
    public void deleteById(Long id) {
        thirdPartyRepository.deleteById(id);
    }
    
    /**
     * Verifica si existe un tercero con el correo electrónico especificado.
     * 
     * @param mail el correo electrónico a verificar
     * @return true si existe un tercero con ese correo, false en caso contrario
     */
    public boolean existsByMail(String mail) {
        return thirdPartyRepository.existsByMail(mail);
    }
    
    /**
     * Busca terceros por tipo de documento.
     * 
     * @param documentType el tipo de documento (número)
     * @return lista de terceros que coinciden con el tipo de documento
     */
    public List<ThirdParty> findByDocumentType(Integer documentType) {
        return thirdPartyRepository.findByDocumentType(documentType);
    }
    
    /**
     * Busca un tercero por su número de documento.
     * 
     * @param documentNumber el número de documento del tercero
     * @return Optional que contiene el ThirdParty si existe, vacío en caso contrario
     */
    public Optional<ThirdParty> findByDocumentNumber(String documentNumber) {
        return thirdPartyRepository.findByDocumentNumber(documentNumber);
    }
    
    /**
     * Verifica si existe un tercero con el número de documento especificado.
     * 
     * @param documentNumber el número de documento a verificar
     * @return true si existe un tercero con ese número de documento, false en caso contrario
     */
    public boolean existsByDocumentNumber(String documentNumber) {
        return thirdPartyRepository.existsByDocumentNumber(documentNumber);
    }
    
    /**
     * Busca terceros que son proveedores.
     * 
     * @param isSupplier true para buscar proveedores
     * @return lista de terceros que son proveedores
     */
    public List<ThirdParty> findByIsSupplier(Boolean isSupplier) {
        return thirdPartyRepository.findByIsSupplier(isSupplier);
    }
    
    /**
     * Busca terceros que son clientes.
     * 
     * @param isClient true para buscar clientes
     * @return lista de terceros que son clientes
     */
    public List<ThirdParty> findByIsClient(Boolean isClient) {
        return thirdPartyRepository.findByIsClient(isClient);
    }
    
    /**
     * Busca terceros que son tanto proveedores como clientes.
     * 
     * @param isSupplier true para buscar proveedores
     * @param isClient true para buscar clientes
     * @return lista de terceros que cumplen ambas condiciones
     */
    public List<ThirdParty> findByIsSupplierAndIsClient(Boolean isSupplier, Boolean isClient) {
        return thirdPartyRepository.findByIsSupplierAndIsClient(isSupplier, isClient);
    }
    
    /**
     * Busca terceros por ciudad.
     * 
     * @param city la ciudad donde se encuentra el tercero
     * @return lista de terceros que están en la ciudad especificada
     */
    public List<ThirdParty> findByCity(String city) {
        return thirdPartyRepository.findByCity(city);
    }
    
    /**
     * Busca terceros por nombre comercial (búsqueda parcial, insensible a mayúsculas).
     * 
     * @param businessName el nombre comercial del tercero
     * @return lista de terceros que coinciden con el nombre comercial
     */
    public List<ThirdParty> findByBusinessNameContaining(String businessName) {
        return thirdPartyRepository.findByBusinessNameContainingIgnoreCase(businessName);
    }
    
    /**
     * Busca terceros por nombre y apellido.
     * 
     * @param name el nombre del tercero
     * @param surname el apellido del tercero
     * @return lista de terceros que coinciden con el nombre y apellido
     */
    public List<ThirdParty> findByNameAndSurname(String name, String surname) {
        return thirdPartyRepository.findByNameAndSurname(name, surname);
    }
    
    /**
     * Obtiene el conteo total de terceros registrados.
     * 
     * @return el número total de terceros
     */
    public long count() {
        return thirdPartyRepository.count();
    }
}
