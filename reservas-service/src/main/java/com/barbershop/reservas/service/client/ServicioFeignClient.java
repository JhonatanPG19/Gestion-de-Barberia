package com.barbershop.reservas.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.barbershop.reservas.dto.external.ServicioDto;

@FeignClient(name = "servicio-service", url = "${services.servicio.url}")
public interface ServicioFeignClient {
    
    @GetMapping("/api/v1/servicios/{id}")
    ServicioDto obtenerServicio(@PathVariable("id") Long id);
    
    @GetMapping("/api/v1/servicios/{id}/existe")
    Boolean existeServicio(@PathVariable("id") Long id);
    
    @GetMapping("/api/v1/servicios/{servicioId}/barberos/{barberoId}/habilitado")
    Boolean barberoPuedeRealizar(
            @PathVariable("servicioId") Long servicioId,
            @PathVariable("barberoId") Long barberoId);
}
