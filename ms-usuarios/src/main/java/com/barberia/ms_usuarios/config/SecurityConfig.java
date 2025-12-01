package com.barberia.ms_usuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                    .requestMatchers("/api/v1/usuarios/**").permitAll()
                    // Permite acceso libre a endpoints que empiecen con /public
                    .requestMatchers("/api/v1/public/**").permitAll()
                    // Todo lo demás requiere autenticación (Token válido)
                    .anyRequest().authenticated()
                )
                // Configurar validación de Token JWT (Oauth2 Resource Server)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
                        // AQUÍ CONECTAMOS NUESTRO CONVERTIDOR PERSONALIZADO
                        jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                ));

        return http.build();
    }


    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        // 1. Le decimos que use el campo 'preferred_username' como nombre de usuario (en vez del ID largo)
        converter.setPrincipalClaimName("preferred_username");
        // 2. Le decimos cómo extraer los roles
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return converter;
    }

    // Clase interna para extraer los roles del JSON de Keycloak
    class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            // Keycloak pone los roles dentro de: realm_access -> roles
            Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");

            if (realmAccess == null || realmAccess.isEmpty()) {
                return List.of();
            }

            List<String> roles = (List<String>) realmAccess.get("roles");

            return roles.stream()
                    .map(roleName -> "ROLE_" + roleName) // Transformamos "CLIENTE" a "ROLE_CLIENTE"
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
    }

}