package com.example.pib2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security para la aplicación.
 * 
 * Esta clase configura:
 * - Autenticación HTTP Basic
 * - Autorización basada en roles (ADMIN, USER)
 * - Codificación de contraseñas con BCrypt
 * - Políticas de sesión stateless para APIs REST
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Configura la cadena de filtros de seguridad.
     * 
     * Define qué endpoints requieren autenticación y qué roles pueden acceder.
     * Configura autenticación HTTP Basic y política de sesión stateless.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF para APIs REST
            .csrf(csrf -> csrf.disable())
            
            // Configurar autorización de requests
            .authorizeHttpRequests(authz -> authz
                // Endpoints públicos (sin autenticación)
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                
                // Endpoints de Swagger/OpenAPI (públicos)
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-resources/**").permitAll()
                .requestMatchers("/webjars/**").permitAll()
                
                // Endpoints que requieren rol ADMIN
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers("/api/items/**").hasAnyRole("ADMIN", "USER")
                
                // Endpoints que requieren rol USER o ADMIN
                .requestMatchers("/api/loans/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/api/loan-history/**").hasAnyRole("ADMIN", "USER")
                
                // Cualquier otro request requiere autenticación
                .anyRequest().authenticated()
            )
            
            // Configurar autenticación HTTP Basic
            .httpBasic(basic -> basic.realmName("PI Backend API"))
            
            // Configurar política de sesión como stateless (para APIs REST)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configurar headers para H2 Console (desarrollo)
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            );
            
        return http.build();
    }

    /**
     * Bean para codificación de contraseñas usando BCrypt.
     * 
     * BCrypt es un algoritmo de hash seguro que incluye salt automático
     * y es resistente a ataques de fuerza bruta.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean para el AuthenticationManager.
     * 
     * Necesario para la autenticación programática y testing.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configura el proveedor de autenticación DAO.
     * 
     * Este proveedor utiliza el UserDetailsService personalizado
     * y el PasswordEncoder para validar credenciales.
     * 
     * Nota: En Spring Security 6.1+, se recomienda usar la configuración
     * directamente en el SecurityFilterChain en lugar de un bean separado.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}