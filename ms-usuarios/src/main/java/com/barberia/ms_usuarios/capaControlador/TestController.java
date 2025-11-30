package com.barberia.ms_usuarios.capaControlador;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

@RequestMapping("/api/v1")
public class TestController {

    // Puerta Abierta: Cualquiera puede entrar
    @GetMapping("/public/hola")
    public ResponseEntity<String> saludoPublico() {
        return ResponseEntity.ok("¡Hola! Soy un endpoint PÚBLICO. No necesitas token.");
    }

    // Puerta Cerrada: Solo con Token de Keycloak
    @GetMapping("/private/perfil")
    public ResponseEntity<String> perfilPrivado() {
        return ResponseEntity.ok("¡ÉXITO! Si lees esto, tu token de Keycloak es válido.");
    }
}