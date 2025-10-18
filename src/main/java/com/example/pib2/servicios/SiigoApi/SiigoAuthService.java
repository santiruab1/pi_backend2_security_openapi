package com.example.pib2.servicios.SiigoApi;

import org.springframework.stereotype.Service;

@Service
public class SiigoAuthService {
    
    private String currentAccessToken;
    
    public String getAccessToken() {
        if (currentAccessToken == null || isTokenExpired()) {
             // Código para solicitar el token a Siigo.
             currentAccessToken = "FAKE_SIIGO_ACCESS_TOKEN"; 
        }
        return currentAccessToken;
    }
    
    private boolean isTokenExpired() {
        // Lógica para verificar si el token actual ha caducado.
        return false;
    }
}

//para que la aplicación se autentique, Siigo requiere el username (nombre de usuario) y access_key (clave de acceso).
//se debe remplazar el token falso en getAccessToken() por el código que realiza la llamada HTTP.