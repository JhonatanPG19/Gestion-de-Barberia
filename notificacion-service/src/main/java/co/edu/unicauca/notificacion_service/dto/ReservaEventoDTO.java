package co.edu.unicauca.notificacion_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

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
    private String estado; // PENDIENTE, CONFIRMADA, EN_PROCESO, COMPLETADA, CANCELADA, NO_ASISTIO
    private Integer tiempoEstimado;
    private String observaciones;
    private boolean esWalkIn;
    private String tipoEvento; // CREADA, CONFIRMADA, CANCELADA, REPROGRAMADA, INICIADO, COMPLETADO
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
