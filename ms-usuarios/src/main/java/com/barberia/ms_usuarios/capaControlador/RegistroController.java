package com.barberia.ms_usuarios.capaControlador;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barberia.ms_usuarios.capaFachada.dto.RegistroUsuarioDTO;
import com.barberia.ms_usuarios.capaFachada.dto.UsuarioResponseDTO;
import com.barberia.ms_usuarios.capaFachada.service.IUsuarioService;
import com.barberia.ms_usuarios.dominio.Usuario;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/usuarios")
public class RegistroController {

    private final IUsuarioService usuarioService; // Inyectamos la Interfaz, no la implementación

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody RegistroUsuarioDTO dto) {
        try {
            System.out.println("=== INICIO REGISTRO ===");
            System.out.println("Username: " + dto.getUsername());
            System.out.println("Correo: " + dto.getCorreo());
            System.out.println("Rol: " + dto.getRol());

            Usuario usuarioCreado = usuarioService.registrarCliente(dto);

            System.out.println("=== USUARIO CREADO CON ID: " + usuarioCreado.getId() + " ===");
            return ResponseEntity.status(201)
                    .body("Usuario registrado con ID: " + usuarioCreado.getId());

        } catch (RuntimeException e) {
            System.out.println("=== ERROR RuntimeException ===");
            e.printStackTrace();  // <-- IMPORTANTE: Esto mostrará el stack trace completo
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println("=== ERROR Exception ===");
            e.printStackTrace();  // <-- IMPORTANTE
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerMiPerfil() {
        // 1. Obtener quién es el usuario autenticado desde el Token JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 2. Preparar una respuesta JSON simulada
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Acceso Autorizado - Datos Privados");
        respuesta.put("usuario", auth.getName()); // Keycloak pone aquí el ID o username
        respuesta.put("roles", auth.getAuthorities().toString());

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/admin/crear-empleado")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Usuario> registrarEmpleado(@RequestBody RegistroUsuarioDTO dto) {
        Usuario creado = usuarioService.registrarEmpleado(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // 3. Endpoint PROTEGIDO (Listar usuarios)
    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    // Endpoint para uso interno entre microservicios (Feign)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuarioPorId(@PathVariable Integer id) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);

        UsuarioResponseDTO respuesta = UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .correo(usuario.getCorreo())
                .telefono(usuario.getTelefono())
                .build();

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existeUsuario(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.existeUsuarioPorId(id));
    }


}