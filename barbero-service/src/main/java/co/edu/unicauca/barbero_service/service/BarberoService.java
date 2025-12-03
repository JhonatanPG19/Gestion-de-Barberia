package co.edu.unicauca.barbero_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unicauca.barbero_service.dto.RegistrarBarberoDTO;
import co.edu.unicauca.barbero_service.exception.ResourceNotFoundException;
import co.edu.unicauca.barbero_service.model.Barbero;
import co.edu.unicauca.barbero_service.model.EstadoBarbero;
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

    public Barbero findByUserId(Integer userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado para el usuario con ID: " + userId));
    }

    public boolean estaDisponibleEnFechaHora(Integer id, LocalDate fecha, LocalTime hora) {
        Barbero barbero = findById(id);

        // 1. Verificar si el barbero está activo
        if (barbero.getEstado() == null || !"activo".equalsIgnoreCase(barbero.getEstado().name())) {
            return false;
        }

        // 2. Verificar si es un dia laboral
        Set<String> diasLaborales = Arrays.stream(barbero.getDiasLaborables().split(","))
                .map(String::trim)
                .map(nombre -> nombre.toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());

        String diaActual = fecha.getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.of("es", "ES"))
                .toUpperCase(Locale.ROOT);

        if (!diasLaborales.contains(diaActual)) {
            return false;
        }

        // 3.Verificar horario laboral
        if (hora.isBefore(barbero.getHorarioInicioLaboral()) ||
                hora.isAfter(barbero.getHorarioFinLaboral())) {
            return false;
        }

        // 4. Verificar horario de descanso
        if (barbero.getHoraInicioDescanso() != null &&
                barbero.getHoraFinDescanso() != null) {
            if (!hora.isBefore(barbero.getHoraInicioDescanso()) &&
                    !hora.isAfter(barbero.getHoraFinDescanso())) {
                return false; // Esta en descanso
            }
        }
        return true;
    }

    public boolean existeBarbero(Integer id) {
        try {
            findById(id);
            return true;
        } catch (ResourceNotFoundException ex) {
            return false;
        }
    }

    // src/main/java/co/edu/unicauca/barbero_service/service/BarberoService.java

    @Transactional
    public void eliminarFisico(Integer id) {
        repository.deleteById(id);
    }

    @Transactional
    public Barbero crear(Barbero barbero) {
        barbero.setId(null);
        barbero.setCreatedAt(LocalDateTime.now());
        return repository.save(barbero);
    }

    @Transactional
    public Barbero create(Barbero barbero) {
        // ✅ Validar que el email no exista
        if (repository.existsByEmail(barbero.getEmail())) {
            throw new IllegalArgumentException("Ya existe un barbero con ese email: " + barbero.getEmail());
        }

        barbero.setId(null);
        barbero.setCreatedAt(LocalDateTime.now());
        barbero.setUpdatedAt(LocalDateTime.now());
        
        // Si no tiene estado, asignar activo por defecto
        if (barbero.getEstado() == null) {
            barbero.setEstado(EstadoBarbero.activo);
        }

        return repository.save(barbero);
    }

    @Transactional
    public Barbero update(Integer id, Barbero barberoDetails) {
        Barbero existing = findById(id);
        
        if (barberoDetails.getUserId() != null) {
            existing.setUserId(barberoDetails.getUserId());
        }
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
        existing.setEstado(EstadoBarbero.inactivo);
        existing.setUpdatedAt(LocalDateTime.now());
        repository.save(existing);
    }

    @Transactional
    public Barbero registrarDesdeDTO(RegistrarBarberoDTO dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Ya existe un barbero con ese email: " + dto.getEmail());
        }

        Barbero nuevo = new Barbero();
        nuevo.setNombre(dto.getNombre());
        nuevo.setTelefono(dto.getTelefono());
        nuevo.setEmail(dto.getEmail());
        nuevo.setEstado(EstadoBarbero.activo); // Por defecto
        nuevo.setCreatedAt(LocalDateTime.now());
        // Horarios por defecto o nulos
        nuevo.setHorarioInicioLaboral(LocalTime.of(8, 0));
        nuevo.setHorarioFinLaboral(LocalTime.of(18, 0));
        nuevo.setDiasLaborables("Lunes,Martes,Miercoles,Jueves,Viernes,Sabado");

        return repository.save(nuevo);
    }

    @Transactional
    public void actualizarEstado(Integer id, EstadoBarbero nuevoEstado) {
        Barbero barbero = findById(id);
        barbero.setEstado(nuevoEstado);
        barbero.setUpdatedAt(LocalDateTime.now());
        repository.save(barbero);
    }

}
