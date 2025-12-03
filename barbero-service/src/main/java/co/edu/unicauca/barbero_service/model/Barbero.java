package co.edu.unicauca.barbero_service.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "barberos")
public class Barbero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId; // ID del usuario en ms-usuarios con rol BARBERO

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String telefono;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoBarbero estado = EstadoBarbero.activo;

    @Column(name = "horario_inicio_laboral")
    private LocalTime horarioInicioLaboral = LocalTime.of(8, 0);

    @Column(name = "horario_fin_laboral")
    private LocalTime horarioFinLaboral = LocalTime.of(18, 0);

    @Column(name = "hora_inicio_descanso")
    private LocalTime horaInicioDescanso;

    @Column(name = "hora_fin_descanso")
    private LocalTime horaFinDescanso;

    @NotBlank
    @Column(name = "dias_laborables")
    private String diasLaborables = "Lunes,Martes,Miercoles,Jueves,Viernes,Sabado";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EstadoBarbero getEstado() {
        return estado;
    }

    public void setEstado(EstadoBarbero estado) {
        this.estado = estado;
    }

    public LocalTime getHorarioInicioLaboral() {
        return horarioInicioLaboral;
    }

    public void setHorarioInicioLaboral(LocalTime horarioInicioLaboral) {
        this.horarioInicioLaboral = horarioInicioLaboral;
    }

    public LocalTime getHorarioFinLaboral() {
        return horarioFinLaboral;
    }

    public void setHorarioFinLaboral(LocalTime horarioFinLaboral) {
        this.horarioFinLaboral = horarioFinLaboral;
    }

    public LocalTime getHoraInicioDescanso() {
        return horaInicioDescanso;
    }

    public void setHoraInicioDescanso(LocalTime horaInicioDescanso) {
        this.horaInicioDescanso = horaInicioDescanso;
    }

    public LocalTime getHoraFinDescanso() {
        return horaFinDescanso;
    }

    public void setHoraFinDescanso(LocalTime horaFinDescanso) {
        this.horaFinDescanso = horaFinDescanso;
    }

    public String getDiasLaborables() {
        return diasLaborables;
    }

    public void setDiasLaborables(String diasLaborables) {
        this.diasLaborables = diasLaborables;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
