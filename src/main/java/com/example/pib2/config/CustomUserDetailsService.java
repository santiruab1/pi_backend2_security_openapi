package com.example.pib2.config;

import com.example.pib2.models.entities.User;
import com.example.pib2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio personalizado para cargar detalles de usuario para Spring Security.
 * 
 * Esta clase implementa UserDetailsService y se encarga de:
 * - Buscar usuarios en la base de datos por username
 * - Convertir la entidad User en un objeto UserDetails
 * - Manejar casos donde el usuario no existe
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Carga un usuario por su identification para autenticación.
     * 
     * Este método es llamado automáticamente por Spring Security
     * durante el proceso de autenticación.
     * 
     * @param identification La identificación del usuario a buscar
     * @return UserDetails El objeto con los detalles del usuario
     * @throws UsernameNotFoundException Si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String identification) throws UsernameNotFoundException {
        User user = userRepository.findByIdentification(identification)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "Usuario no encontrado con identification: " + identification));
        
        return user;
    }
}