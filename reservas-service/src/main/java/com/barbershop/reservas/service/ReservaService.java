package com.barbershop.reservas.service;

import com.barbershop.reservas.dto.request.CrearReservaRequest;
import com.barbershop.reservas.dto.request.ActualizarReservaRequest;
import com.barbershop.reservas.dto.response.ReservaResponse;
import com.barbershop.reservas.entities.ReservaEntity;
import com.barbershop.reservas.entities.enums.EstadoReserva;
import com.barbershop.reservas.service.client.BarberoFeignClient;
import com.barbershop.reservas.service.client.ClienteFeignClient;
import com.barbershop.reservas.service.client.ServicioFeignClient;
import com.barbershop.reservas.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final BarberoFeignClient barberoFeignClient;
    private final ServicioFeignClient servicioFeignClient;
    private final ClienteFeignClient clienteFeignClient;
    private final NotificacionService notificacionService;

    private static final int TIEMPO_BUFFER_MINUTOS = 10;
    private static final int TOLERANCIA_RETRASO_MINUTOS = 10;
    private static final LocalTime HORA_APERTURA = LocalTime.of(8, 0);
    private static final LocalTime HORA_CIERRE = LocalTime.of(20, 0);

    public ReservaResponse crearReserva(CrearReservaRequest request) {
        log.info("Creando reserva para cliente: {}", request.getClienteId());
        
        // Validaciones
        validarReserva(request);
        
        // Obtener información del servicio para calcular duración
        var servicioInfo = servicioFeignClient.obtenerServicio(request.getServicioId());
        
        // Crear entidad
        ReservaEntity reserva = ReservaEntity.builder()
                .clienteId(request.getClienteId())
                .barberoId(request.getBarberoId())
                .servicioId(request.getServicioId())
                .fechaHora(request.getFechaHora())
                .tiempoEstimado(servicioInfo.getDuracion())
                .observaciones(request.getObservaciones())
                .esWalkIn(request.getEsWalkIn() != null ? request.getEsWalkIn() : false)
                .estado(EstadoReserva.PENDIENTE)
                .build();
        
        // Calcular fecha/hora fin
        LocalDateTime fechaHoraFin = request.getFechaHora()
                .plusMinutes(servicioInfo.getDuracion())
                .plusMinutes(TIEMPO_BUFFER_MINUTOS);
        reserva.setFechaHoraFin(fechaHoraFin);
        
        // Verificar disponibilidad
        if (!verificarDisponibilidad(request.getBarberoId(), request.getFechaHora(), servicioInfo.getDuracion())) {
            throw new IllegalStateException("El barbero no está disponible en el horario solicitado");
        }
        
        // Guardar reserva
        ReservaEntity reservaGuardada = reservaRepository.save(reserva);
        
        // Enviar notificación
        try {
            notificacionService.enviarNotificacionReservaCreada(reservaGuardada);
        } catch (Exception e) {
            log.error("Error al enviar notificación de reserva creada", e);
        }
        
        log.info("Reserva creada exitosamente con ID: {}", reservaGuardada.getId());
        return mapearAResponse(reservaGuardada);
    }

    public ReservaResponse actualizarReserva(Long id, ActualizarReservaRequest request) {
        log.info("Actualizando reserva ID: {}", id);
        
        ReservaEntity reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        
        if (request.getObservaciones() != null) {
            reserva.setObservaciones(request.getObservaciones());
        }
        
        ReservaEntity reservaActualizada = reservaRepository.save(reserva);
        return mapearAResponse(reservaActualizada);
    }

    public void cancelarReserva(Long id, String motivo) {
        log.info("Cancelando reserva ID: {}", id);
        
        ReservaEntity reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        
        if (!reserva.puedeSerCancelada()) {
            throw new IllegalStateException("La reserva no puede ser cancelada en su estado actual");
        }
        
        reserva.cancelar();
        if (motivo != null) {
            reserva.setObservaciones(motivo);
        }
        
        reservaRepository.save(reserva);
        
        // Enviar notificación
        try {
            notificacionService.enviarNotificacionReservaCancelada(reserva, motivo);
        } catch (Exception e) {
            log.error("Error al enviar notificación de cancelación", e);
        }
    }

    public void confirmarReserva(Long id) {
        log.info("Confirmando reserva ID: {}", id);
        
        ReservaEntity reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        
        reserva.confirmar();
        reservaRepository.save(reserva);
        
        // Enviar notificación
        try {
            notificacionService.enviarNotificacionReservaConfirmada(reserva);
        } catch (Exception e) {
            log.error("Error al enviar notificación de confirmación", e);
        }
    }

    public void iniciarServicio(Long id) {
        log.info("Iniciando servicio para reserva ID: {}", id);
        
        ReservaEntity reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        
        reserva.enProceso();
        reservaRepository.save(reserva);
        
        // Enviar notificación
        try {
            notificacionService.enviarNotificacionServicioIniciado(reserva);
        } catch (Exception e) {
            log.error("Error al enviar notificación de inicio de servicio", e);
        }
    }

    public void completarServicio(Long id) {
        log.info("Completando servicio para reserva ID: {}", id);
        
        ReservaEntity reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        
        reserva.completar();
        reservaRepository.save(reserva);
        
        // Enviar notificación
        try {
            notificacionService.enviarNotificacionServicioCompletado(reserva);
        } catch (Exception e) {
            log.error("Error al enviar notificación de servicio completado", e);
        }
    }

    public void marcarNoAsistio(Long id) {
        log.info("Marcando no asistió para reserva ID: {}", id);
        
        ReservaEntity reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        
        reserva.noAsistio();
        reservaRepository.save(reserva);
    }

    @Transactional(readOnly = true)
    public Optional<ReservaResponse> obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id)
                .map(this::mapearAResponse);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> obtenerReservasPorCliente(Long clienteId) {
        return reservaRepository.findByClienteId(clienteId).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> obtenerReservasPorBarbero(Long barberoId, LocalDate fecha) {
        return reservaRepository.findByBarberoIdAndFecha(barberoId, fecha).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> obtenerReservasDelDia(LocalDate fecha) {
        return reservaRepository.findByFecha(fecha).stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LocalDateTime> obtenerHorariosDisponibles(Long barberoId, Long servicioId, LocalDate fecha) {
        log.info("Obteniendo horarios disponibles para barbero: {} en fecha: {}", barberoId, fecha);
        
        var servicioInfo = servicioFeignClient.obtenerServicio(servicioId);
        int duracionServicio = servicioInfo.getDuracion();
        
        List<LocalDateTime> horariosDisponibles = new ArrayList<>();
        LocalDateTime inicioJornada = fecha.atTime(HORA_APERTURA);
        LocalDateTime finJornada = fecha.atTime(HORA_CIERRE);
        
        LocalDateTime horaActual = inicioJornada;
        
        while (horaActual.plusMinutes(duracionServicio).isBefore(finJornada) || 
               horaActual.plusMinutes(duracionServicio).equals(finJornada)) {
            
            if (verificarDisponibilidad(barberoId, horaActual, duracionServicio)) {
                horariosDisponibles.add(horaActual);
            }
            
            horaActual = horaActual.plusMinutes(30); // Intervalos de 30 minutos
        }
        
        return horariosDisponibles;
    }

    @Transactional(readOnly = true)
    public boolean verificarDisponibilidad(Long barberoId, LocalDateTime fechaHora, int duracion) {
        LocalDateTime fechaHoraFin = fechaHora.plusMinutes(duracion).plusMinutes(TIEMPO_BUFFER_MINUTOS);
        
        return !reservaRepository.existsByBarberoIdAndFechaHoraOverlap(
                barberoId, fechaHora, fechaHoraFin);
    }

    public void reprogramarReserva(Long id, LocalDateTime nuevaFechaHora) {
        log.info("Reprogramando reserva ID: {} a nueva fecha: {}", id, nuevaFechaHora);
        
        ReservaEntity reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        
        if (!reserva.puedeSerCancelada()) {
            throw new IllegalStateException("La reserva no puede ser reprogramada en su estado actual");
        }
        
        // Verificar disponibilidad en nuevo horario
        var servicioInfo = servicioFeignClient.obtenerServicio(reserva.getServicioId());
        
        if (!verificarDisponibilidad(reserva.getBarberoId(), nuevaFechaHora, servicioInfo.getDuracion())) {
            throw new IllegalStateException("El barbero no está disponible en el nuevo horario");
        }
        
        reserva.setFechaHora(nuevaFechaHora);
        reserva.setFechaHoraFin(nuevaFechaHora.plusMinutes(servicioInfo.getDuracion()).plusMinutes(TIEMPO_BUFFER_MINUTOS));
        
        reservaRepository.save(reserva);
        
        // Enviar notificación
        try {
            notificacionService.enviarNotificacionReservaReprogramada(reserva);
        } catch (Exception e) {
            log.error("Error al enviar notificación de reprogramación", e);
        }
    }

    private void validarReserva(CrearReservaRequest request) {
        // Validar que el cliente existe
        if (!clienteFeignClient.existeCliente(request.getClienteId())) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }
        
        // Validar que el barbero existe
        if (!barberoFeignClient.existeBarbero(request.getBarberoId())) {
            throw new IllegalArgumentException("Barbero no encontrado");
        }
        
        // Validar que el servicio existe
        if (!servicioFeignClient.existeServicio(request.getServicioId())) {
            throw new IllegalArgumentException("Servicio no encontrado");
        }
        
        // Validar que la fecha es futura
        if (request.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se pueden crear reservas en el pasado");
        }
        
        // Validar horario de atención
        LocalTime horaReserva = request.getFechaHora().toLocalTime();
        if (horaReserva.isBefore(HORA_APERTURA) || horaReserva.isAfter(HORA_CIERRE)) {
            throw new IllegalArgumentException("La reserva debe estar dentro del horario de atención");
        }
    }

    private ReservaResponse mapearAResponse(ReservaEntity entity) {
        ReservaResponse response = ReservaResponse.builder()
                .id(entity.getId())
                .clienteId(entity.getClienteId())
                .barberoId(entity.getBarberoId())
                .servicioId(entity.getServicioId())
                .fechaHora(entity.getFechaHora())
                .fechaHoraFin(entity.getFechaHoraFin())
                .estado(entity.getEstado())
                .tiempoEstimado(entity.getTiempoEstimado())
                .observaciones(entity.getObservaciones())
                .fechaCreacion(entity.getFechaCreacion())
                .esWalkIn(entity.isEsWalkIn())
                .build();
        
        // Enriquecer con información de otros servicios
        try {
            var cliente = clienteFeignClient.obtenerCliente(entity.getClienteId());
            response.setClienteNombre(cliente.getNombre());
        } catch (Exception e) {
            log.warn("No se pudo obtener información del cliente", e);
        }
        
        try {
            var barbero = barberoFeignClient.obtenerBarbero(entity.getBarberoId());
            response.setBarberoNombre(barbero.getNombre());
        } catch (Exception e) {
            log.warn("No se pudo obtener información del barbero", e);
        }
        
        try {
            var servicio = servicioFeignClient.obtenerServicio(entity.getServicioId());
            response.setServicioNombre(servicio.getNombre());
        } catch (Exception e) {
            log.warn("No se pudo obtener información del servicio", e);
        }
        
        return response;
    }
}
