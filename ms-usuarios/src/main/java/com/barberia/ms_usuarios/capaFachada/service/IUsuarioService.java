package com.barberia.ms_usuarios.capaFachada.service;

import com.barberia.ms_usuarios.dominio.Usuario;
import com.barberia.ms_usuarios.capaFachada.dto.RegistroUsuarioDTO;


public interface IUsuarioService {
    Usuario registrarUsuario(RegistroUsuarioDTO dto);
}