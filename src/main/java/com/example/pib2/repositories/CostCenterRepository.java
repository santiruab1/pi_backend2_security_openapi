package com.example.pib2.repositories;

import com.example.pib2.models.entities.CostCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad CostCenter.
 * 
 * Extiende JpaRepository para operaciones CRUD básicas y define
 * métodos de consulta personalizados para centros de costo.
 */
@Repository
public interface CostCenterRepository extends JpaRepository<CostCenter, Long> {
    
    /**
     * Busca un centro de costo por su código.
     * 
     * @param code El código del centro de costo a buscar
     * @return Optional<CostCenter> El centro de costo si existe, Optional.empty() si no
     */
    Optional<CostCenter> findByCode(String code);
    
    /**
     * Busca un centro de costo por su código, excluyendo un ID específico.
     * 
     * Útil para validaciones de unicidad durante actualizaciones.
     * 
     * @param code El código del centro de costo a buscar
     * @param id El ID a excluir de la búsqueda
     * @return Optional<CostCenter> El centro de costo si existe, Optional.empty() si no
     */
    Optional<CostCenter> findByCodeAndIdNot(String code, Long id);
    
    /**
     * Busca centros de costo por nombre (búsqueda parcial, case insensitive).
     * 
     * @param name El nombre o parte del nombre a buscar
     * @return List<CostCenter> Lista de centros de costo que coinciden
     */
    @Query("SELECT c FROM CostCenter c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<CostCenter> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Busca centros de costo activos.
     * 
     * @return List<CostCenter> Lista de centros de costo activos
     */
    List<CostCenter> findByActiveTrue();
    
    /**
     * Busca centros de costo inactivos.
     * 
     * @return List<CostCenter> Lista de centros de costo inactivos
     */
    List<CostCenter> findByActiveFalse();
    
    /**
     * Verifica si existe un centro de costo con el código dado.
     * 
     * @param code El código a verificar
     * @return boolean true si existe, false si no
     */
    boolean existsByCode(String code);
    
    /**
     * Verifica si existe un centro de costo con el código dado, excluyendo un ID específico.
     * 
     * @param code El código a verificar
     * @param id El ID a excluir de la verificación
     * @return boolean true si existe, false si no
     */
    boolean existsByCodeAndIdNot(String code, Long id);
}
