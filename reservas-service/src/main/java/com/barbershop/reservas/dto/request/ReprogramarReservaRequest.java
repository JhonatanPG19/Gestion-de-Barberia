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
public class ReprogramarReservaRequest {
    
    @NotNull(message = "La nueva fecha y hora son requeridas")
    @Future(message = "La fecha debe ser futura")
    private LocalDateTime nuevaFechaHora;
}
