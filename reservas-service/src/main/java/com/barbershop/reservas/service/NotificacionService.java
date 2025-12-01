package com.barbershop.reservas.service;

import com.barbershop.reservas.dto.event.ReservaEventoDTO;
import com.barbershop.reservas.dto.external.BarberoDto;
import com.barbershop.reservas.dto.external.ClienteDto;
import com.barbershop.reservas.dto.external.ServicioDto;
import com.barbershop.reservas.entities.ReservaEntity;
import com.barbershop.reservas.service.client.BarberoFeignClient;
import com.barbershop.reservas.service.client.ClienteFeignClient;
import com.barbershop.reservas.service.client.ServicioFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    private final RabbitTemplate rabbitTemplate;
    private final ClienteFeignClient clienteFeignClient;
    private final BarberoFeignClient barberoFeignClient;
    private final ServicioFeignClient servicioFeignClient;

    @Value("${rabbitmq.exchange.notificaciones}")
    private String exchangeNotificaciones;

    @Value("${rabbitmq.routing-key.reserva-creada}")
    private String routingKeyReservaCreada;

    @Value("${rabbitmq.routing-key.reserva-confirmada}")
    private String routingKeyReservaConfirmada;

    @Value("${rabbitmq.routing-key.reserva-cancelada}")
    private String routingKeyReservaCancelada;

    @Value("${rabbitmq.routing-key.reserva-reprogramada}")
    private String routingKeyReservaReprogramada;

    @Value("${rabbitmq.routing-key.servicio-iniciado}")
    private String routingKeyServicioIniciado;

    @Value("${rabbitmq.routing-key.servicio-completado}")
    private String routingKeyServicioCompletado;

    public void enviarNotificacionReservaCreada(ReservaEntity reserva) {
        publicarEvento(reserva, "CREADA", routingKeyReservaCreada);
    }

    public void enviarNotificacionReservaConfirmada(ReservaEntity reserva) {
        publicarEvento(reserva, "CONFIRMADA", routingKeyReservaConfirmada);
    }

    public void enviarNotificacionReservaCancelada(ReservaEntity reserva, String motivo) {
        publicarEvento(reserva, "CANCELADA", routingKeyReservaCancelada, motivo);
    }

    public void enviarNotificacionReservaReprogramada(ReservaEntity reserva) {
        publicarEvento(reserva, "REPROGRAMADA", routingKeyReservaReprogramada);
    }

    public void enviarNotificacionServicioIniciado(ReservaEntity reserva) {
        publicarEvento(reserva, "SERVICIO_INICIADO", routingKeyServicioIniciado);
    }

    public void enviarNotificacionServicioCompletado(ReservaEntity reserva) {
        publicarEvento(reserva, "SERVICIO_COMPLETADO", routingKeyServicioCompletado);
    }

    private void publicarEvento(ReservaEntity reserva, String tipoEvento, String routingKey) {
        publicarEvento(reserva, tipoEvento, routingKey, null);
    }

    private void publicarEvento(ReservaEntity reserva, String tipoEvento, String routingKey, String motivo) {
        try {
            ReservaEventoDTO evento = construirEvento(reserva, tipoEvento, motivo);
            rabbitTemplate.convertAndSend(exchangeNotificaciones, routingKey, evento);
            log.info("Evento {} publicado en routingKey {} para reserva {}", tipoEvento, routingKey, reserva.getId());
        } catch (Exception ex) {
            log.error("No se pudo publicar evento {} para reserva {}", tipoEvento, reserva.getId(), ex);
        }
    }

    private ReservaEventoDTO construirEvento(ReservaEntity reserva, String tipoEvento, String motivo) {
        ClienteDto cliente = obtenerCliente(reserva.getClienteId());
        BarberoDto barbero = obtenerBarbero(reserva.getBarberoId());
        ServicioDto servicio = obtenerServicio(reserva.getServicioId());

        return ReservaEventoDTO.builder()
                .id(reserva.getId())
                .clienteId(reserva.getClienteId())
                .clienteNombre(cliente != null ? cliente.getNombre() : null)
                .clienteEmail(cliente != null ? cliente.getEmail() : null)
                .barberoId(reserva.getBarberoId())
                .barberoNombre(barbero != null ? barbero.getNombre() : null)
                .servicioId(reserva.getServicioId())
                .servicioNombre(servicio != null ? servicio.getNombre() : null)
                .fechaHora(reserva.getFechaHora())
                .fechaHoraFin(reserva.getFechaHoraFin())
                .estado(reserva.getEstado().name())
                .tiempoEstimado(reserva.getTiempoEstimado())
                .observaciones(motivo != null ? motivo : reserva.getObservaciones())
                .esWalkIn(reserva.isEsWalkIn())
                .tipoEvento(tipoEvento)
                .fechaCreacion(reserva.getFechaCreacion())
                .fechaActualizacion(reserva.getFechaActualizacion())
                .build();
    }

    private ClienteDto obtenerCliente(Long id) {
        try {
            return clienteFeignClient.obtenerCliente(id);
        } catch (Exception ex) {
            log.warn("No se pudo obtener información del cliente {}: {}", id, ex.getMessage());
            return null;
        }
    }

    private BarberoDto obtenerBarbero(Long id) {
        try {
            return barberoFeignClient.obtenerBarbero(id);
        } catch (Exception ex) {
            log.warn("No se pudo obtener información del barbero {}: {}", id, ex.getMessage());
            return null;
        }
    }

    private ServicioDto obtenerServicio(Long id) {
        try {
            return servicioFeignClient.obtenerServicio(id);
        } catch (Exception ex) {
            log.warn("No se pudo obtener información del servicio {}: {}", id, ex.getMessage());
            return null;
        }
    }
}
