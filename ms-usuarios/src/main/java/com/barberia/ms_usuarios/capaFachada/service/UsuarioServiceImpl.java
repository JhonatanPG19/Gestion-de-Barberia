package com.barberia.ms_usuarios.capaFachada.service;


import com.barberia.ms_usuarios.capaFachada.dto.RegistroUsuarioDTO;
import com.barberia.ms_usuarios.dominio.Usuario;
import com.barberia.ms_usuarios.capaAccesoADatos.repository.IUsuarioRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor; // <--- IMPORTANTE
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioRepository usuarioRepository;

    private final KeycloakService keycloakService;

    @Override
    @Transactional // Si falla algo, hace rollback en la DB
    public Usuario registrarUsuario(RegistroUsuarioDTO dto) {

        // 1. Validaciones previas locales
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("El username ya existe en la base de datos local");
        }
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new RuntimeException("El correo ya existe en la base de datos local");
        }

        // 2. Crear usuario en Keycloak (IAM)
        // Nota: Asegúrate de actualizar tu KeycloakService para aceptar el rol del DTO
        keycloakService.crearUsuario(dto);

        // En UsuarioServiceImpl.java
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(dto.getUsername());
        nuevoUsuario.setCorreo(dto.getCorreo()); // Getter nuevo
        nuevoUsuario.setPassword(dto.getPassword());
        nuevoUsuario.setRol(dto.getRol());

        return usuarioRepository.save(nuevoUsuario);
    }
}