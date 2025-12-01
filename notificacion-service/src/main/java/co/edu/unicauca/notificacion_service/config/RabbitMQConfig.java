package co.edu.unicauca.notificacion_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.notificaciones}")
    private String exchange;

    @Value("${rabbitmq.queue.reserva-creada}")
    private String queueReservaCreada;

    @Value("${rabbitmq.queue.reserva-confirmada}")
    private String queueReservaConfirmada;

    @Value("${rabbitmq.queue.reserva-cancelada}")
    private String queueReservaCancelada;

    @Value("${rabbitmq.queue.reserva-reprogramada}")
    private String queueReservaReprogramada;

    @Value("${rabbitmq.queue.servicio-iniciado}")
    private String queueServicioIniciado;

    @Value("${rabbitmq.queue.servicio-completado}")
    private String queueServicioCompletado;

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

    // Exchange
    @Bean
    public TopicExchange notificacionesExchange() {
        return new TopicExchange(exchange);
    }

    // Queues
    @Bean
    public Queue reservaCreadaQueue() {
        return new Queue(queueReservaCreada, true);
    }

    @Bean
    public Queue reservaConfirmadaQueue() {
        return new Queue(queueReservaConfirmada, true);
    }

    @Bean
    public Queue reservaCanceladaQueue() {
        return new Queue(queueReservaCancelada, true);
    }

    @Bean
    public Queue reservaReprogramadaQueue() {
        return new Queue(queueReservaReprogramada, true);
    }

    @Bean
    public Queue servicioIniciadoQueue() {
        return new Queue(queueServicioIniciado, true);
    }

    @Bean
    public Queue servicioCompletadoQueue() {
        return new Queue(queueServicioCompletado, true);
    }

    // Bindings
    @Bean
    public Binding reservaCreadaBinding() {
        return BindingBuilder
                .bind(reservaCreadaQueue())
                .to(notificacionesExchange())
                .with(routingKeyReservaCreada);
    }

    @Bean
    public Binding reservaConfirmadaBinding() {
        return BindingBuilder
                .bind(reservaConfirmadaQueue())
                .to(notificacionesExchange())
                .with(routingKeyReservaConfirmada);
    }

    @Bean
    public Binding reservaCanceladaBinding() {
        return BindingBuilder
                .bind(reservaCanceladaQueue())
                .to(notificacionesExchange())
                .with(routingKeyReservaCancelada);
    }

    @Bean
    public Binding reservaReprogramadaBinding() {
        return BindingBuilder
                .bind(reservaReprogramadaQueue())
                .to(notificacionesExchange())
                .with(routingKeyReservaReprogramada);
    }

    @Bean
    public Binding servicioIniciadoBinding() {
        return BindingBuilder
                .bind(servicioIniciadoQueue())
                .to(notificacionesExchange())
                .with(routingKeyServicioIniciado);
    }

    @Bean
    public Binding servicioCompletadoBinding() {
        return BindingBuilder
                .bind(servicioCompletadoQueue())
                .to(notificacionesExchange())
                .with(routingKeyServicioCompletado);
    }

    // Message Converter
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }
}
