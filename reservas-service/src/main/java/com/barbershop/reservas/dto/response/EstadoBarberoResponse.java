package com.barbershop.reservas.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

/**
 * Representa el estado operativo en tiempo real de un barbero.
 */
@Value
@Builder
public class EstadoBarberoResponse {
    Long barberoId;
    String estado; // OCUPADO o DISPONIBLE
    boolean ocupado;
    Long reservaActualId;
    LocalDateTime reservaActualInicio;
    LocalDateTime reservaActualFin;
    Long proximaReservaId;
    LocalDateTime proximaReservaInicio;
}
