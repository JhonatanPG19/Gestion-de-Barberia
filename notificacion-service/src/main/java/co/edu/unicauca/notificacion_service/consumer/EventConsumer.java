package co.edu.unicauca.notificacion_service.consumer;

import co.edu.unicauca.notificacion_service.dto.ReservaEventoDTO;
import co.edu.unicauca.notificacion_service.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {

    private final NotificacionService notificacionService;

    @RabbitListener(queues = "${rabbitmq.queue.reserva-creada}", containerFactory = "rabbitListenerContainerFactory")
    public void consumirEventoReservaCreada(ReservaEventoDTO evento) {
        try {
            log.info("Evento recibido - Reserva Creada: {}", evento);
            notificacionService.procesarEventoReservaCreada(evento);
        } catch (Exception e) {
            log.error("Error procesando evento de reserva creada: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.reserva-confirmada}", containerFactory = "rabbitListenerContainerFactory")
    public void consumirEventoReservaConfirmada(ReservaEventoDTO evento) {
        try {
            log.info("Evento recibido - Reserva Confirmada: {}", evento);
            notificacionService.procesarEventoReservaCreada(evento);
        } catch (Exception e) {
            log.error("Error procesando evento de reserva confirmada: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.reserva-cancelada}", containerFactory = "rabbitListenerContainerFactory")
    public void consumirEventoReservaCancelada(ReservaEventoDTO evento) {
        try {
            log.info("Evento recibido - Reserva Cancelada: {}", evento);
            notificacionService.procesarEventoReservaCancelada(evento);
        } catch (Exception e) {
            log.error("Error procesando evento de reserva cancelada: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.reserva-reprogramada}", containerFactory = "rabbitListenerContainerFactory")
    public void consumirEventoReservaReprogramada(ReservaEventoDTO evento) {
        try {
            log.info("Evento recibido - Reserva Reprogramada: {}", evento);
            notificacionService.procesarEventoReservaModificada(evento);
        } catch (Exception e) {
            log.error("Error procesando evento de reserva reprogramada: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.servicio-iniciado}", containerFactory = "rabbitListenerContainerFactory")
    public void consumirEventoServicioIniciado(ReservaEventoDTO evento) {
        try {
            log.info("Evento recibido - Servicio Iniciado: {}", evento);
            // Puedes crear un nuevo método en NotificacionService para este evento
            notificacionService.procesarEventoReservaCreada(evento);
        } catch (Exception e) {
            log.error("Error procesando evento de servicio iniciado: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.servicio-completado}", containerFactory = "rabbitListenerContainerFactory")
    public void consumirEventoServicioCompletado(ReservaEventoDTO evento) {
        try {
            log.info("Evento recibido - Servicio Completado: {}", evento);
            // Puedes crear un nuevo método en NotificacionService para este evento
            notificacionService.procesarEventoReservaCreada(evento);
        } catch (Exception e) {
            log.error("Error procesando evento de servicio completado: {}", e.getMessage(), e);
        }
    }
}
