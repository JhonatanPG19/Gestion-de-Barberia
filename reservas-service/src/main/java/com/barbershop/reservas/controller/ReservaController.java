package com.barbershop.reservas.controller;

import com.barbershop.reservas.dto.request.ActualizarReservaRequest;
import com.barbershop.reservas.dto.request.CrearReservaRequest;
import com.barbershop.reservas.dto.request.ReprogramarReservaRequest;
import com.barbershop.reservas.dto.response.ReservaResponse;
import com.barbershop.reservas.dto.response.EstadoBarberoResponse;
import com.barbershop.reservas.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reservas", description = "API para gestión de reservas y turnos")
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    @Operation(summary = "Crear una nueva reserva")
    public ResponseEntity<ReservaResponse> crearReserva(@Valid @RequestBody CrearReservaRequest request) {
        log.info("Recibiendo solicitud para crear reserva: {}", request);
        ReservaResponse response = reservaService.crearReserva(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva por ID")
    public ResponseEntity<ReservaResponse> obtenerReservaPorId(@PathVariable Long id) {
        return reservaService.obtenerReservaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Obtener todas las reservas de un cliente")
    public ResponseEntity<List<ReservaResponse>> obtenerReservasPorCliente(@PathVariable Long clienteId) {
        List<ReservaResponse> reservas = reservaService.obtenerReservasPorCliente(clienteId);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/barbero/{barberoId}")
    @Operation(summary = "Obtener reservas de un barbero por fecha")
    public ResponseEntity<List<ReservaResponse>> obtenerReservasPorBarbero(
            @PathVariable Long barberoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        List<ReservaResponse> reservas = reservaService.obtenerReservasPorBarbero(barberoId, fecha);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/barbero/{barberoId}/estado")
    @Operation(summary = "Consultar estado en tiempo real del barbero")
    public ResponseEntity<EstadoBarberoResponse> obtenerEstadoEnTiempoReal(@PathVariable Long barberoId) {
        return ResponseEntity.ok(reservaService.obtenerEstadoBarbero(barberoId));
    }

    @GetMapping("/dia/{fecha}")
    @Operation(summary = "Obtener todas las reservas del día")
    public ResponseEntity<List<ReservaResponse>> obtenerReservasDelDia(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        List<ReservaResponse> reservas = reservaService.obtenerReservasDelDia(fecha);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/disponibilidad")
    @Operation(summary = "Obtener horarios disponibles")
    public ResponseEntity<List<LocalDateTime>> obtenerHorariosDisponibles(
            @RequestParam Long barberoId,
            @RequestParam Long servicioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        List<LocalDateTime> horarios = reservaService.obtenerHorariosDisponibles(barberoId, servicioId, fecha);
        return ResponseEntity.ok(horarios);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una reserva")
    public ResponseEntity<ReservaResponse> actualizarReserva(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarReservaRequest request) {
        
        ReservaResponse response = reservaService.actualizarReserva(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar una reserva")
    public ResponseEntity<Void> confirmarReserva(@PathVariable Long id) {
        reservaService.confirmarReserva(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/iniciar")
    @Operation(summary = "Iniciar el servicio de una reserva")
    public ResponseEntity<Void> iniciarServicio(@PathVariable Long id) {
        reservaService.iniciarServicio(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/completar")
    @Operation(summary = "Completar el servicio de una reserva")
    public ResponseEntity<Void> completarServicio(@PathVariable Long id) {
        reservaService.completarServicio(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/no-asistio")
    @Operation(summary = "Marcar cliente como no asistido")
    public ResponseEntity<Void> marcarNoAsistio(@PathVariable Long id) {
        reservaService.marcarNoAsistio(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/reprogramar")
    @Operation(summary = "Reprogramar una reserva")
    public ResponseEntity<Void> reprogramarReserva(
            @PathVariable Long id,
            @Valid @RequestBody ReprogramarReservaRequest request) {
        
        reservaService.reprogramarReserva(id, request.getNuevaFechaHora());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar una reserva")
    public ResponseEntity<Void> cancelarReserva(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        
        reservaService.cancelarReserva(id, motivo);
        return ResponseEntity.noContent().build();
    }
}
