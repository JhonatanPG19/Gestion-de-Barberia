package com.barberia.ms_usuarios.capaFachada.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String username;
    private String password;
}
