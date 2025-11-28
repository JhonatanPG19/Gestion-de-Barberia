package com.barbershop.reservas.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarberoDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String especialidad;
    private boolean activo;
}
