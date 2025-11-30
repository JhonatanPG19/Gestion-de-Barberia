package com.barbershop.reservas.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearReservaRequest {

    @NotNull(message = "El ID del cliente es requerido")
    private Long clienteId;

    @NotNull(message = "El ID del barbero es requerido")
    private Long barberoId;

    @NotNull(message = "El ID del servicio es requerido")
    private Long servicioId;

    @NotNull(message = "La fecha y hora son requeridas")
    @Future(message = "La fecha debe ser futura")
    private LocalDateTime fechaHora;

    private String observaciones;

    @Builder.Default
    private Boolean esWalkIn = false;
}
