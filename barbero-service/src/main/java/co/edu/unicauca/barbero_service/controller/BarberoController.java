package co.edu.unicauca.barbero_service.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.barbero_service.dto.RegistrarBarberoDTO;
import co.edu.unicauca.barbero_service.model.Barbero;
import co.edu.unicauca.barbero_service.model.EstadoBarbero;
import co.edu.unicauca.barbero_service.service.BarberoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/barbero")
public class BarberoController {

    @Autowired
    private BarberoService service;

    @PostMapping("/create")
    public ResponseEntity<Barbero> crear(@Valid @RequestBody Barbero barbero) {
        Barbero saved = service.create(barbero);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/crear")
    public ResponseEntity<Barbero> create(@Valid @RequestBody Barbero barbero) {
        Barbero saved = service.create(barbero); // ✅ Ahora incluye validación
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Barbero>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Barbero>> getByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(service.findByEstado(estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Barbero> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Barbero> update(@PathVariable Integer id, @Valid @RequestBody Barbero barbero) {
        Barbero updated = service.update(id, barbero);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/registrarDto")
    public ResponseEntity<Barbero> registrar(@Valid @RequestBody RegistrarBarberoDTO dto) {
        Barbero nuevoBarbero = service.registrarDesdeDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoBarbero);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Void> actualizarEstado(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        String nuevoEstadoStr = body.get("estado");
        EstadoBarbero nuevoEstado = EstadoBarbero.valueOf(nuevoEstadoStr.toLowerCase()); // mayúsculas
        service.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para validar disponibilidad de un barbero en una fecha especifica
    @GetMapping("/{id}/disponibilidad")
    public ResponseEntity<Map<String, Boolean>> consultarDispoibilidad(
            @PathVariable Integer id,
            @RequestParam LocalDate fecha,
            @RequestParam LocalTime hora) {
        boolean disponible = service.estaDisponibleEnFechaHora(id, fecha, hora);
        return ResponseEntity.ok(Map.of("disponible", disponible));
    }

    // Endpoint pra validar si un barbero existe
    @GetMapping("/existe/{id}")
    public ResponseEntity<Map<String, Boolean>> existeBarbero(@PathVariable Integer id) {
        boolean existe = service.existeBarbero(id);
        return ResponseEntity.ok(Map.of("existe", existe));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarFisico(@PathVariable Integer id) {
        service.eliminarFisico(id);
        return ResponseEntity.noContent().build();
    }

}
