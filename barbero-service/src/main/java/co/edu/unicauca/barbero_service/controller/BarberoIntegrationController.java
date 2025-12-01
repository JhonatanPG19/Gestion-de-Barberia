package co.edu.unicauca.barbero_service.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.barbero_service.dto.BarberoResumenDTO;
import co.edu.unicauca.barbero_service.service.BarberoService;

/**
 * Endpoints diseñados para consumo del microservicio de reservas.
 */
@RestController
@RequestMapping("/api/v1/barberos")
public class BarberoIntegrationController {

    private final BarberoService barberoService;

    public BarberoIntegrationController(BarberoService barberoService) {
        this.barberoService = barberoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BarberoResumenDTO> obtenerBarbero(@PathVariable Integer id) {
        return ResponseEntity.ok(BarberoResumenDTO.fromEntity(barberoService.findById(id)));
    }

    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existeBarbero(@PathVariable Integer id) {
        return ResponseEntity.ok(barberoService.existeBarbero(id));
    }

    @GetMapping("/{id}/disponibilidad")
    public ResponseEntity<Boolean> verificarDisponibilidad(
            @PathVariable Integer id,
            @RequestParam("fechaHora") String fechaHoraIso) {
        try {
            LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraIso);
            boolean disponible = barberoService.estaDisponibleEnFechaHora(
                    id,
                    fechaHora.toLocalDate(),
                    fechaHora.toLocalTime()
            );
            return ResponseEntity.ok(disponible);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().body(Boolean.FALSE);
        }
    }
}
