package co.edu.unicauca.barbero_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.barbero_service.model.Barbero;
import co.edu.unicauca.barbero_service.service.BarberoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/barbero")
public class BarberoController {

    @Autowired
    private BarberoService service;

    @PostMapping
    public ResponseEntity<Barbero> create(@Valid @RequestBody Barbero barbero) {
        Barbero saved = service.create(barbero);
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
}
