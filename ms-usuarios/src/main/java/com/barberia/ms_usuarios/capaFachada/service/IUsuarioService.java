package com.barberia.ms_usuarios.capaFachada.service;

import java.util.List;

import com.barberia.ms_usuarios.capaFachada.dto.LoginRequestDTO;
import com.barberia.ms_usuarios.capaFachada.dto.LoginResponseDTO;
import com.barberia.ms_usuarios.capaFachada.dto.RegistroUsuarioDTO;
import com.barberia.ms_usuarios.dominio.Usuario;


public interface IUsuarioService {


    Usuario registrarCliente(RegistroUsuarioDTO dto);

    // Para el Admin (Puede crear BARBERO o ADMIN)
    Usuario registrarEmpleado(RegistroUsuarioDTO dto);

    // Para listar (Admin)
    List<Usuario> listarUsuarios();

    Usuario obtenerUsuarioPorId(Integer id);

    boolean existeUsuarioPorId(Integer id);

    // Login personalizado
    LoginResponseDTO login(LoginRequestDTO loginRequest);
}