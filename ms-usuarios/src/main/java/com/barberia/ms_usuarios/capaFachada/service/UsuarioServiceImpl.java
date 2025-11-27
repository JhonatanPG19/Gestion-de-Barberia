package com.barberia.ms_usuarios.capaFachada.service;

import com.barberia.ms_usuarios.capaFachada.service.KeycloakService; // <-- Verifica esta ruta
// O si KeycloakService está en otro paquete, impórtalo bien.
import com.barberia.ms_usuarios.capaFachada.dto.RegistroUsuarioDTO;
import com.barberia.ms_usuarios.dominio.Usuario;
import com.barberia.ms_usuarios.capaAccesoADatos.repository.IUsuarioRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor; // <--- IMPORTANTE
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioRepository usuarioRepository;

    private final KeycloakService keycloakService;

    @Transactional
    public Usuario registrarUsuarioPrivado(RegistroUsuarioDTO dto) {
        System.out.println(">>> Verificando username: " + dto.getUsername());

        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("El username ya existe en la base de datos local");
        }

        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new RuntimeException("El correo ya existe en la base de datos local");
        }

        System.out.println(">>> Creando usuario en Keycloak con rol: " + dto.getRol());
        keycloakService.crearUsuario(dto);
        System.out.println(">>> Usuario creado en Keycloak OK");

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(dto.getUsername());
        nuevoUsuario.setNombre(dto.getNombre());
        nuevoUsuario.setApellido(dto.getApellido());
        nuevoUsuario.setTelefono(dto.getTelefono());
        nuevoUsuario.setCorreo(dto.getCorreo());
        nuevoUsuario.setPassword(dto.getPassword());
        nuevoUsuario.setRol(dto.getRol());

        System.out.println(">>> Guardando en PostgreSQL...");
        Usuario guardado = usuarioRepository.save(nuevoUsuario);
        System.out.println(">>> Guardado con ID: " + guardado.getId());

        return guardado;
    }
    @Override
    public Usuario registrarCliente(RegistroUsuarioDTO dto) {
        // FUERZA BRUTA: No importa qué manden, aquí es CLIENTE
        dto.setRol("cliente");
        return registrarUsuarioPrivado(dto); // Reutiliza tu lógica actual de guardar/keycloak
    }

    @Override
    public Usuario registrarEmpleado(RegistroUsuarioDTO dto) {
        // Aquí SÍ respetamos el rol que viene en el DTO (porque quien llama es el Admin)
        return registrarUsuarioPrivado(dto);
    }

    @Override
    public List<Usuario> listarUsuarios()
    {
        return usuarioRepository.findAll();
    }
}