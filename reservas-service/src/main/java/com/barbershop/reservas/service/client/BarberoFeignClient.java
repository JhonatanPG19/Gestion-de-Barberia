package com.barbershop.reservas.service.client;

import com.barbershop.reservas.dto.external.BarberoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "barbero-service", url = "${services.barbero.url}")
public interface BarberoFeignClient {
    
    @GetMapping("/api/v1/barberos/{id}")
    BarberoDto obtenerBarbero(@PathVariable("id") Long id);
    
    @GetMapping("/api/v1/barberos/{id}/disponibilidad")
    Boolean verificarDisponibilidad(
            @PathVariable("id") Long id,
            @RequestParam("fechaHora") String fechaHora
    );
    
    @GetMapping("/api/v1/barberos/{id}/existe")
    Boolean existeBarbero(@PathVariable("id") Long id);
}
