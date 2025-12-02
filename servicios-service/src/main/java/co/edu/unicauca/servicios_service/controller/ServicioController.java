package co.edu.unicauca.servicios_service.controller;

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

import co.edu.unicauca.servicios_service.model.Servicio;
import co.edu.unicauca.servicios_service.service.ServicioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    @Autowired
    private ServicioService service;

    @PostMapping
    public ResponseEntity<Servicio> create(@Valid @RequestBody Servicio servicio) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(servicio));
    }

    @GetMapping
    public ResponseEntity<List<Servicio>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servicio> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Servicio> update(@PathVariable Integer id, @Valid @RequestBody Servicio servicio) {
        return ResponseEntity.ok(service.update(id, servicio));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/barberos")
    public ResponseEntity<List<Integer>> obtenerBarberos(@PathVariable Integer id) {
        return ResponseEntity.ok(service.obtenerBarberosDeServicio(id));
    }

    // Con este añade un nuevo barbero, pero se pierden los barberos anteriormente
    @PutMapping("/{id}/barberos")
    public ResponseEntity<Void> asignarBarberos(
            @PathVariable Integer id,
            @RequestBody List<Integer> barberosIds) {
        service.asignarBarberos(id, barberosIds);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/barberos")
    public ResponseEntity<Void> agregarBarbero(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> body) {
        Integer barberoId = body.get("barberoId");
        service.agregarBarberoAServicio(id, barberoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/existe/{id}")
    public ResponseEntity<Map<String, Boolean>> existeServicio (@PathVariable Integer id) {
        boolean existe = service.existeServicio(id);
        return ResponseEntity.ok(Map.of("existe", existe));
    }
    
}
