package co.edu.unicauca.barbero_service.dto;

import co.edu.unicauca.barbero_service.model.Barbero;

/**
 * DTO ligero expuesto al microservicio de reservas para obtener datos del barbero.
 */
public class BarberoResumenDTO {

    private Integer id;
    private String nombre;
    private String telefono;
    private String email;
    private String estado;

    public static BarberoResumenDTO fromEntity(Barbero barbero) {
        BarberoResumenDTO dto = new BarberoResumenDTO();
        dto.setId(barbero.getId());
        dto.setNombre(barbero.getNombre());
        dto.setTelefono(barbero.getTelefono());
        dto.setEmail(barbero.getEmail());
        dto.setEstado(barbero.getEstado() != null ? barbero.getEstado().name() : null);
        return dto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
