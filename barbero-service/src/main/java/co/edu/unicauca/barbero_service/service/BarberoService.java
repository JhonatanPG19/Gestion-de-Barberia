package co.edu.unicauca.barbero_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unicauca.barbero_service.exception.ResourceNotFoundException;
import co.edu.unicauca.barbero_service.model.Barbero;
import co.edu.unicauca.barbero_service.repository.BarberoRepository;

@Service
public class BarberoService {

    @Autowired
    private BarberoRepository repository;

    public List<Barbero> findAll() {
        return repository.findAll();
    }

    public List<Barbero> findByEstado(String estado) {
        return repository.findByEstado(estado);
    }

    public Barbero findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado con ID: " + id));
    }

    @Transactional
    public Barbero create(Barbero barbero) {
        barbero.setId(null);
        barbero.setCreatedAt(LocalDateTime.now());
        return repository.save(barbero);
    }

    @Transactional
    public Barbero update(Integer id, Barbero barberoDetails) {
        Barbero existing = findById(id);
        existing.setNombre(barberoDetails.getNombre());
        existing.setTelefono(barberoDetails.getTelefono());
        existing.setEmail(barberoDetails.getEmail());
        existing.setEstado(barberoDetails.getEstado());
        existing.setHorarioInicioLaboral(barberoDetails.getHorarioInicioLaboral());
        existing.setHorarioFinLaboral(barberoDetails.getHorarioFinLaboral());
        existing.setHoraInicioDescanso(barberoDetails.getHoraInicioDescanso());
        existing.setHoraFinDescanso(barberoDetails.getHoraFinDescanso());
        existing.setDiasLaborables(barberoDetails.getDiasLaborables());
        existing.setUpdatedAt(LocalDateTime.now());
        return repository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        Barbero existing = findById(id);
        existing.setEstado("inactivo");
        existing.setUpdatedAt(LocalDateTime.now());
        repository.save(existing);
    }
}
