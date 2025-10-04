package com.example.pib2.servicios;

import com.example.pib2.models.entities.CostCenter;
import com.example.pib2.repositories.CostCenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de centros de costo.
 * 
 * Contiene la lógica de negocio para operaciones CRUD y validaciones
 * relacionadas con centros de costo.
 */
@Service
public class CostCenterService {
    
    @Autowired
    private CostCenterRepository costCenterRepository;

    /**
     * Obtiene todos los centros de costo.
     * 
     * @return List<CostCenter> Lista de todos los centros de costo
     */
    public List<CostCenter> findAll() {
        return costCenterRepository.findAll();
    }

    /**
     * Obtiene todos los centros de costo activos.
     * 
     * @return List<CostCenter> Lista de centros de costo activos
     */
    public List<CostCenter> findActive() {
        return costCenterRepository.findByActiveTrue();
    }

    /**
     * Busca un centro de costo por su ID.
     * 
     * @param id El ID del centro de costo
     * @return Optional<CostCenter> El centro de costo si existe
     */
    public Optional<CostCenter> findById(Long id) {
        return costCenterRepository.findById(id);
    }

    /**
     * Busca un centro de costo por su código.
     * 
     * @param code El código del centro de costo
     * @return Optional<CostCenter> El centro de costo si existe
     */
    public Optional<CostCenter> findByCode(String code) {
        return costCenterRepository.findByCode(code);
    }

    /**
     * Busca centros de costo por nombre (búsqueda parcial).
     * 
     * @param name El nombre o parte del nombre a buscar
     * @return List<CostCenter> Lista de centros de costo que coinciden
     */
    public List<CostCenter> findByNameContaining(String name) {
        return costCenterRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Guarda un centro de costo.
     * 
     * @param costCenter El centro de costo a guardar
     * @return CostCenter El centro de costo guardado
     * @throws IllegalArgumentException Si el código ya existe
     */
    public CostCenter save(CostCenter costCenter) {
        // Validar que el código sea único
        if (costCenter.getId() == null) {
            // Creación: verificar que el código no exista
            if (costCenterRepository.existsByCode(costCenter.getCode())) {
                throw new IllegalArgumentException("Ya existe un centro de costo con el código: " + costCenter.getCode());
            }
        } else {
            // Actualización: verificar que el código no exista en otros registros
            if (costCenterRepository.existsByCodeAndIdNot(costCenter.getCode(), costCenter.getId())) {
                throw new IllegalArgumentException("Ya existe un centro de costo con el código: " + costCenter.getCode());
            }
        }
        
        return costCenterRepository.save(costCenter);
    }

    /**
     * Actualiza un centro de costo existente.
     * 
     * @param id El ID del centro de costo a actualizar
     * @param costCenter Los nuevos datos del centro de costo
     * @return Optional<CostCenter> El centro de costo actualizado si existe
     * @throws IllegalArgumentException Si el código ya existe en otro registro
     */
    public Optional<CostCenter> update(Long id, CostCenter costCenter) {
        return costCenterRepository.findById(id)
                .map(existing -> {
                    // Validar que el código sea único (excluyendo el registro actual)
                    if (costCenterRepository.existsByCodeAndIdNot(costCenter.getCode(), id)) {
                        throw new IllegalArgumentException("Ya existe un centro de costo con el código: " + costCenter.getCode());
                    }
                    
                    existing.setCode(costCenter.getCode());
                    existing.setName(costCenter.getName());
                    existing.setActive(costCenter.getActive());
                    return costCenterRepository.save(existing);
                });
    }

    /**
     * Elimina un centro de costo por su ID.
     * 
     * @param id El ID del centro de costo a eliminar
     * @return boolean true si se eliminó, false si no existía
     */
    public boolean deleteById(Long id) {
        if (costCenterRepository.existsById(id)) {
            costCenterRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Verifica si existe un centro de costo con el código dado.
     * 
     * @param code El código a verificar
     * @return boolean true si existe, false si no
     */
    public boolean existsByCode(String code) {
        return costCenterRepository.existsByCode(code);
    }

    /**
     * Activa o desactiva un centro de costo.
     * 
     * @param id El ID del centro de costo
     * @param active El nuevo estado activo/inactivo
     * @return Optional<CostCenter> El centro de costo actualizado si existe
     */
    public Optional<CostCenter> setActive(Long id, boolean active) {
        return costCenterRepository.findById(id)
                .map(costCenter -> {
                    costCenter.setActive(active);
                    return costCenterRepository.save(costCenter);
                });
    }
}
