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
    
    private Long reservaId;
    private Long clienteId;
    private String clienteNombre;
    private String clienteEmail;
    private Long barberoId;
    private String barberoNombre;
    private LocalDateTime fechaHora;
    private String servicio;
    private String tipoEvento; // CREADA, CANCELADA, MODIFICADA
    private String motivoCancelacion;
}
