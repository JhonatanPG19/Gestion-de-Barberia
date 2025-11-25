package com.barberia.ms_usuarios.capaControlador;



import com.barberia.ms_usuarios.capaFachada.dto.RegistroUsuarioDTO;
import com.barberia.ms_usuarios.dominio.Usuario;
import com.barberia.ms_usuarios.capaFachada.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/usuarios")
public class RegistroController {


    private final IUsuarioService usuarioService; // Inyectamos la Interfaz, no la implementación

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody RegistroUsuarioDTO dto) {
        try {
            Usuario usuarioCreado = usuarioService.registrarUsuario(dto);
            return ResponseEntity.status(201).body("Usuario registrado con ID: " + usuarioCreado.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }
}