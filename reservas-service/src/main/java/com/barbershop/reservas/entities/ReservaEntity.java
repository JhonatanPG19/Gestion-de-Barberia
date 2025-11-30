package com.barbershop.reservas.entities;

import com.barbershop.reservas.entities.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservas", indexes = {
    @Index(name = "idx_cliente_id", columnList = "cliente_id"),
    @Index(name = "idx_barbero_id", columnList = "barbero_id"),
    @Index(name = "idx_fecha_hora", columnList = "fecha_hora"),
    @Index(name = "idx_estado", columnList = "estado")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;
    
    @Column(name = "barbero_id", nullable = false)
    private Long barberoId;
    
    @Column(name = "servicio_id", nullable = false)
    private Long servicioId;
    
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;
    
    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado;
    
    @Column(name = "tiempo_estimado")
    private Integer tiempoEstimado;
    
    @Column(length = 500)
    private String observaciones;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @Column(name = "es_walk_in")
    private boolean esWalkIn;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    public void confirmar() {
        this.estado = EstadoReserva.CONFIRMADA;
    }
    
    public void cancelar() {
        this.estado = EstadoReserva.CANCELADA;
    }
    
    public void completar() {
        this.estado = EstadoReserva.COMPLETADA;
    }
    
    public void enProceso() {
        this.estado = EstadoReserva.EN_PROCESO;
    }
    
    public void noAsistio() {
        this.estado = EstadoReserva.NO_ASISTIO;
    }
    
    public boolean puedeSerCancelada() {
        return estado == EstadoReserva.PENDIENTE || estado == EstadoReserva.CONFIRMADA;
    }
    
    public boolean estaRetrasado(int minutosTolerancia) {
        if (fechaHora == null) return false;
        LocalDateTime horaLimite = fechaHora.plusMinutes(minutosTolerancia);
        return LocalDateTime.now().isAfter(horaLimite) && 
               (estado == EstadoReserva.PENDIENTE || estado == EstadoReserva.CONFIRMADA);
    }
}
