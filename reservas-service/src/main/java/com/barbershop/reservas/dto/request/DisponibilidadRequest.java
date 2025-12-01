package com.barbershop.reservas.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadRequest {
    
    @NotNull(message = "El ID del barbero es requerido")
    private Long barberoId;
    
    @NotNull(message = "El ID del servicio es requerido")
    private Long servicioId;
    
    @NotNull(message = "La fecha es requerida")
    private LocalDate fecha;
}
