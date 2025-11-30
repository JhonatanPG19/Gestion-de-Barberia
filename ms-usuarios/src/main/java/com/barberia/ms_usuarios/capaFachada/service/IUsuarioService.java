package com.barberia.ms_usuarios.capaFachada.service;

import com.barberia.ms_usuarios.dominio.Usuario;
import com.barberia.ms_usuarios.capaFachada.dto.RegistroUsuarioDTO;

import java.util.List;


public interface IUsuarioService {


    Usuario registrarCliente(RegistroUsuarioDTO dto);

    // Para el Admin (Puede crear BARBERO o ADMIN)
    Usuario registrarEmpleado(RegistroUsuarioDTO dto);

    // Para listar (Admin)
    List<Usuario> listarUsuarios();

    Usuario obtenerUsuarioPorId(Integer id);
}