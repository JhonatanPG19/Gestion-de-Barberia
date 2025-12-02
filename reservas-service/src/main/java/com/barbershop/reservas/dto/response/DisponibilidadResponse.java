package com.barbershop.reservas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadResponse {
    private LocalDate fecha;
    private Long barberoId;
    private String barberoNombre;
    private List<HorarioDisponible> horariosDisponibles;
}
