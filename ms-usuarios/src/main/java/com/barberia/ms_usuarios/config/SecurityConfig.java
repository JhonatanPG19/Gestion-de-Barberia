package com.barberia.ms_usuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (No es necesario en APIs REST stateless)
                .csrf(csrf -> csrf.disable())

                // Configurar reglas de acceso
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/usuarios/registro").permitAll()
                        // Permite acceso libre a endpoints que empiecen con /public
                        .requestMatchers("/api/v1/public/**").permitAll()
                        // Todo lo demás requiere autenticación (Token válido)
                        .anyRequest().authenticated()
                )

                // Configurar validación de Token JWT (Oauth2 Resource Server)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}