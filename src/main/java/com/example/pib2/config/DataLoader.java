package com.example.pib2.config;

import com.example.pib2.models.entities.User;
import com.example.pib2.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Componente para cargar datos iniciales en la aplicación.
 * 
 * Esta clase se ejecuta al iniciar la aplicación y crea usuarios
 * de prueba con diferentes roles si no existen en la base de datos.
 */
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Método que se ejecuta al iniciar la aplicación.
     * 
     * Crea usuarios de prueba con roles ADMIN y USER si no existen.
     */
    @Override
    public void run(String... args) throws Exception {
        // Crear usuario ADMIN si no existe
        if (userRepository.findByIdentification("12345678").isEmpty()) {
            User admin = new User();
            admin.setIdentification("12345678");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setFirstName("John");
            admin.setLastName("Doe");
            admin.setEnabled(true);
            admin.setAccountNonExpired(true);
            admin.setAccountNonLocked(true);
            admin.setCredentialsNonExpired(true);
            
            userRepository.save(admin);
            System.out.println("Usuario ADMIN creado: identification=12345678, password=admin123");
        }

        // Crear usuario USER si no existe
        if (userRepository.findByIdentification("87654321").isEmpty()) {
            User user = new User();
            user.setIdentification("87654321");
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole("USER");
            user.setFirstName("Jane");
            user.setLastName("Smith");
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            
            userRepository.save(user);
            System.out.println("Usuario USER creado: identification=87654321, password=user123");
        }

        // Crear otro usuario USER de ejemplo
        if (userRepository.findByIdentification("11223344").isEmpty()) {
            User john = new User();
            john.setIdentification("11223344");
            john.setEmail("john@example.com");
            john.setPassword(passwordEncoder.encode("john123"));
            john.setRole("USER");
            john.setFirstName("John");
            john.setLastName("Johnson");
            john.setEnabled(true);
            john.setAccountNonExpired(true);
            john.setAccountNonLocked(true);
            john.setCredentialsNonExpired(true);
            
            userRepository.save(john);
            System.out.println("Usuario USER creado: identification=11223344, password=john123");
        }

        System.out.println("\n=== CREDENCIALES DE PRUEBA ===");
        System.out.println("ADMIN: identification=12345678, password=admin123");
        System.out.println("USER: identification=87654321, password=user123");
        System.out.println("USER: identification=11223344, password=john123");
        System.out.println("================================\n");
    }
}