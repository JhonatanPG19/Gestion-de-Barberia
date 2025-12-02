package com.barbershop.reservas.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicioDto {
    private Long id;
    private String nombre;
    private Integer duracion;
    private Double precio;
    private String descripcion;
}
