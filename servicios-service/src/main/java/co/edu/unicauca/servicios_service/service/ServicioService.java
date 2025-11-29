package co.edu.unicauca.servicios_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unicauca.servicios_service.exception.ResourceNotFoundException;
import co.edu.unicauca.servicios_service.model.Servicio;
import co.edu.unicauca.servicios_service.repository.ServicioRepository;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository repository;

    public List<Servicio> findAll() {
        return repository.findAll();
    }

    public Servicio findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con ID: " + id));
    }

    @Transactional
    public Servicio create(Servicio servicio) {
        servicio.setId(null);
        servicio.setCreatedAt(LocalDateTime.now());
        return repository.save(servicio);
    }

    @Transactional
    public Servicio update(Integer id, Servicio servicioDetails) {
        Servicio existing = findById(id);
        existing.setNombre(servicioDetails.getNombre());
        existing.setDescripcion(servicioDetails.getDescripcion());
        existing.setDuracionMinutos(servicioDetails.getDuracionMinutos());
        existing.setPrecio(servicioDetails.getPrecio());
        existing.setBarberosIds(servicioDetails.getBarberosIds());
        existing.setUpdatedAt(LocalDateTime.now());
        return repository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        Servicio existing = findById(id);
        existing.setActivo(false);
        existing.setUpdatedAt(LocalDateTime.now());
        repository.save(existing);
    }

    public List<Integer> obtenerBarberosDeServicio(Integer servicioId) {
        Servicio servicio = findById(servicioId);
        return servicio.getBarberosIds();
    }

    // Con este añade un nuevo barbero, pero se pierden los barberos anteriormente
    @Transactional
    public void asignarBarberos(Integer servicioId, List<Integer> barberosIds) {
        Servicio servicio = findById(servicioId);
        servicio.setBarberosIds(barberosIds != null ? barberosIds : new ArrayList<>());
        servicio.setUpdatedAt(LocalDateTime.now());
        repository.save(servicio);
    }

    @Transactional
    public void agregarBarberoAServicio(Integer servicioId, Integer barberoId) {
        Servicio servicio = findById(servicioId);
        if (servicio.getBarberosIds() == null) {
            servicio.setBarberosIds(new ArrayList<>());
        }
        if (!servicio.getBarberosIds().contains(barberoId)) {
            servicio.getBarberosIds().add(barberoId);
            repository.save(servicio);
        }
    }
}
