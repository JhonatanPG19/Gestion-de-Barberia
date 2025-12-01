package co.edu.unicauca.servicios_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.servicios_service.dto.ServicioResumenDTO;
import co.edu.unicauca.servicios_service.service.ServicioService;

/**
 * Endpoints públicos para otros microservicios (por ejemplo, reservas-service).
 */
@RestController
@RequestMapping("/api/v1/servicios")
public class ServicioIntegrationController {

    private final ServicioService servicioService;

    public ServicioIntegrationController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioResumenDTO> obtenerServicio(@PathVariable Integer id) {
        return ResponseEntity.ok(ServicioResumenDTO.fromEntity(servicioService.findById(id)));
    }

    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existeServicio(@PathVariable Integer id) {
        return ResponseEntity.ok(servicioService.existeServicio(id));
    }

    @GetMapping("/{servicioId}/barberos/{barberoId}/habilitado")
    public ResponseEntity<Boolean> barberoPuedeRealizar(
            @PathVariable Integer servicioId,
            @PathVariable Integer barberoId) {
        return ResponseEntity.ok(servicioService.barberoPuedeRealizarServicio(servicioId, barberoId));
    }
}
