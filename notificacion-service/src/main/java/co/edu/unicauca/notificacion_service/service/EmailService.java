package co.edu.unicauca.notificacion_service.service;

import co.edu.unicauca.notificacion_service.model.EstadoNotificacion;
import co.edu.unicauca.notificacion_service.model.Notificacion;
import co.edu.unicauca.notificacion_service.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final NotificacionRepository notificacionRepository;

    @Value("${email.from}")
    private String emailFrom;

    @Value("${email.enabled}")
    private boolean emailEnabled;

    /**
     * Envía un correo electrónico y registra la notificación
     */
    public void enviarEmail(Notificacion notificacion) {
        try {
            if (!emailEnabled) {
                log.warn("Email deshabilitado. No se enviará el email a: {}", notificacion.getDestinatario());
                notificacion.setEstado(EstadoNotificacion.FALLIDA);
                notificacion.setErrorMensaje("Email deshabilitado en configuración");
                notificacionRepository.save(notificacion);
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(notificacion.getDestinatario());
            message.setSubject(notificacion.getAsunto());
            message.setText(notificacion.getMensaje());

            mailSender.send(message);

            notificacion.setEstado(EstadoNotificacion.ENVIADA);
            notificacion.setFechaEnvio(LocalDateTime.now());
            
            log.info("Email enviado exitosamente a: {}", notificacion.getDestinatario());

        } catch (MailException e) {
            log.error("Error al enviar email a: {}. Error: {}", notificacion.getDestinatario(), e.getMessage());
            notificacion.setEstado(EstadoNotificacion.FALLIDA);
            notificacion.setErrorMensaje(e.getMessage());
        } finally {
            notificacionRepository.save(notificacion);
        }
    }

    /**
     * Reintenta enviar notificaciones fallidas
     */
    public void reintentarNotificacionesFallidas() {
        var notificacionesFallidas = notificacionRepository.findByEstado(EstadoNotificacion.FALLIDA);
        
        log.info("Reintentando envío de {} notificaciones fallidas", notificacionesFallidas.size());
        
        notificacionesFallidas.forEach(notificacion -> {
            notificacion.setEstado(EstadoNotificacion.PENDIENTE);
            enviarEmail(notificacion);
        });
    }
}
