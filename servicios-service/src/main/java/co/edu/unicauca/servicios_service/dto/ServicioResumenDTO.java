package co.edu.unicauca.servicios_service.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import co.edu.unicauca.servicios_service.model.Servicio;

/**
 * DTO simplificado expuesto al microservicio de reservas.
 */
public class ServicioResumenDTO {

    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer duracion;
    private BigDecimal precio;
    private Boolean activo;
    private List<Integer> barberosIds;

    public static ServicioResumenDTO fromEntity(Servicio servicio) {
        ServicioResumenDTO dto = new ServicioResumenDTO();
        dto.setId(servicio.getId());
        dto.setNombre(servicio.getNombre());
        dto.setDescripcion(servicio.getDescripcion());
        dto.setDuracion(servicio.getDuracionMinutos());
        dto.setPrecio(servicio.getPrecio());
        dto.setActivo(servicio.getActivo());
        dto.setBarberosIds(servicio.getBarberosIds() != null
                ? new ArrayList<>(servicio.getBarberosIds())
                : new ArrayList<>());
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<Integer> getBarberosIds() {
        return barberosIds;
    }

    public void setBarberosIds(List<Integer> barberosIds) {
        this.barberosIds = barberosIds;
    }
}
