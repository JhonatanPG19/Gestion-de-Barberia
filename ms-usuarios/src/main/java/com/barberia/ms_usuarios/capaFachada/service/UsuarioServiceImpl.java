package com.barberia.ms_usuarios.capaFachada.service;

import java.util.List; // <-- Verifica esta ruta
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barberia.ms_usuarios.capaAccesoADatos.repository.IUsuarioRepository;
import com.barberia.ms_usuarios.capaFachada.dto.LoginRequestDTO;
import com.barberia.ms_usuarios.capaFachada.dto.LoginResponseDTO;
import com.barberia.ms_usuarios.capaFachada.dto.RegistroUsuarioDTO; // <--- IMPORTANTE
import com.barberia.ms_usuarios.dominio.Usuario;
import com.barberia.ms_usuarios.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioRepository usuarioRepository;

    private final KeycloakService keycloakService;

    private final JwtUtil jwtUtil;

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

    public Usuario obtenerUsuarioPorId(Integer id)
    {
        return usuarioRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
    }

    @Override
    public boolean existeUsuarioPorId(Integer id) {
        return usuarioRepository.existsById(id);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        // 1. Buscar usuario por username
        Usuario usuario = usuarioRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        // 2. Verificar contraseña (en texto plano por ahora - en producción usar BCrypt)
        if (!usuario.getPassword().equals(loginRequest.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // 3. Generar token JWT
        String token = jwtUtil.generateToken(usuario.getId(), usuario.getUsername(), usuario.getRol());

        // 4. Construir respuesta
        return LoginResponseDTO.builder()
                .token(token)
                .userId(usuario.getId())
                .username(usuario.getUsername())
                .rol(usuario.getRol())
                .build();
    }
}