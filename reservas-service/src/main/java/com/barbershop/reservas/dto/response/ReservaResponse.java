package com.barbershop.reservas.dto.response;

import com.barbershop.reservas.entities.enums.EstadoReserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponse {
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private Long barberoId;
    private String barberoNombre;
    private Long servicioId;
    private String servicioNombre;
    private LocalDateTime fechaHora;
    private LocalDateTime fechaHoraFin;
    private EstadoReserva estado;
    private Integer tiempoEstimado;
    private String observaciones;
    private LocalDateTime fechaCreacion;
    private boolean esWalkIn;
}
