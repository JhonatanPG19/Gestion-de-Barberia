package com.barberia.ms_usuarios.dominio;

import jakarta.persistence.*;
import lombok.Data;

@Data // Lombok genera Getters, Setters, ToString, etc.
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID Autogenerado (Serial)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // Se guardará, aunque Keycloak maneja la auth real

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String rol;
}