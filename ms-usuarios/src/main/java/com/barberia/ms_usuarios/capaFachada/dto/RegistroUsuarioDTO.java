package com.barberia.ms_usuarios.capaFachada.dto;

import lombok.Data;

@Data
public class RegistroUsuarioDTO {

    private String username;
    private String nombre;
    private String apellido;
    private String telefono;
    private String password;
    private String correo; // En Keycloak esto se mapea a "email"
    private String rol;    // Ej: "CLIENTE", "BARBERO", "ADMIN"
}