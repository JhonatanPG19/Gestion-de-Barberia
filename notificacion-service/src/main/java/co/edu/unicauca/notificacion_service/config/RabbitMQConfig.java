package co.edu.unicauca.notificacion_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.reserva.creada}")
    private String reservaCreadaQueue;

    @Value("${rabbitmq.queue.reserva.cancelada}")
    private String reservaCanceladaQueue;

    @Value("${rabbitmq.queue.reserva.modificada}")
    private String reservaModificadaQueue;

    @Value("${rabbitmq.exchange.reservas}")
    private String reservasExchange;

    @Value("${rabbitmq.routing.key.creada}")
    private String routingKeyCreada;

    @Value("${rabbitmq.routing.key.cancelada}")
    private String routingKeyCancelada;

    @Value("${rabbitmq.routing.key.modificada}")
    private String routingKeyModificada;

    // Queues
    @Bean
    public Queue reservaCreadaQueue() {
        return new Queue(reservaCreadaQueue, true); // durable = true
    }

    @Bean
    public Queue reservaCanceladaQueue() {
        return new Queue(reservaCanceladaQueue, true);
    }

    @Bean
    public Queue reservaModificadaQueue() {
        return new Queue(reservaModificadaQueue, true);
    }

    // Exchange
    @Bean
    public TopicExchange reservasExchange() {
        return new TopicExchange(reservasExchange);
    }

    // Bindings
    @Bean
    public Binding reservaCreadaBinding() {
        return BindingBuilder
                .bind(reservaCreadaQueue())
                .to(reservasExchange())
                .with(routingKeyCreada);
    }

    @Bean
    public Binding reservaCanceladaBinding() {
        return BindingBuilder
                .bind(reservaCanceladaQueue())
                .to(reservasExchange())
                .with(routingKeyCancelada);
    }

    @Bean
    public Binding reservaModificadaBinding() {
        return BindingBuilder
                .bind(reservaModificadaQueue())
                .to(reservasExchange())
                .with(routingKeyModificada);
    }

    // Message Converter
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
