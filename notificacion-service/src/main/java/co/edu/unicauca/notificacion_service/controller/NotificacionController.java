package co.edu.unicauca.notificacion_service.controller;

import co.edu.unicauca.notificacion_service.model.EstadoNotificacion;
import co.edu.unicauca.notificacion_service.model.Notificacion;
import co.edu.unicauca.notificacion_service.model.TipoNotificacion;
import co.edu.unicauca.notificacion_service.repository.NotificacionRepository;
import co.edu.unicauca.notificacion_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionRepository notificacionRepository;
    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<List<Notificacion>> listarTodas() {
        return ResponseEntity.ok(notificacionRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacion> obtenerPorId(@PathVariable Long id) {
        return notificacionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/destinatario/{email}")
    public ResponseEntity<List<Notificacion>> obtenerPorDestinatario(@PathVariable String email) {
        return ResponseEntity.ok(notificacionRepository.findByDestinatario(email));
    }

    @GetMapping("/reserva/{reservaId}")
    public ResponseEntity<List<Notificacion>> obtenerPorReserva(@PathVariable Long reservaId) {
        return ResponseEntity.ok(notificacionRepository.findByReservaId(reservaId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Notificacion>> obtenerPorEstado(@PathVariable EstadoNotificacion estado) {
        return ResponseEntity.ok(notificacionRepository.findByEstado(estado));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Notificacion>> obtenerPorTipo(@PathVariable TipoNotificacion tipo) {
        return ResponseEntity.ok(notificacionRepository.findByTipo(tipo));
    }

    @GetMapping("/rango-fechas")
    public ResponseEntity<List<Notificacion>> obtenerPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(notificacionRepository.findByFechaCreacionBetween(inicio, fin));
    }

    @PostMapping("/reintentar-fallidas")
    public ResponseEntity<String> reintentarFallidas() {
        emailService.reintentarNotificacionesFallidas();
        return ResponseEntity.ok("Proceso de reintento iniciado");
    }
}
