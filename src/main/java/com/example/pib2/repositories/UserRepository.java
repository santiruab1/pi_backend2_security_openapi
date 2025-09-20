package com.example.pib2.repositories;

import com.example.pib2.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio para la entidad User.
 * 
 * Extiende JpaRepository para operaciones CRUD básicas y define
 * métodos de consulta personalizados para Spring Security.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Busca un usuario por su identification.
     * 
     * Este método es utilizado por Spring Security para la autenticación.
     * 
     * @param identification La identificación del usuario a buscar
     * @return Optional<User> El usuario si existe, Optional.empty() si no
     */
    Optional<User> findByIdentification(String identification);
    
    /**
     * Busca un usuario por su email.
     * 
     * Útil para validaciones de unicidad y recuperación de contraseña.
     * 
     * @param email El email del usuario a buscar
     * @return Optional<User> El usuario si existe, Optional.empty() si no
     */
    Optional<User> findByEmail(String email);
}
