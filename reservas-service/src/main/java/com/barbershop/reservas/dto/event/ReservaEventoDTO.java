package com.barbershop.reservas.dto.event;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaEventoDTO implements Serializable {

    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private String clienteEmail;
    private Long barberoId;
    private String barberoNombre;
    private Long servicioId;
    private String servicioNombre;
    private LocalDateTime fechaHora;
    private LocalDateTime fechaHoraFin;
    private String estado;
    private Integer tiempoEstimado;
    private String observaciones;
    private boolean esWalkIn;
    private String tipoEvento;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
