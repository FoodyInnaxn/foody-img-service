package com.foody.imageuploadservice.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String IMG_EXCHANGE = "img_exchange";
    public static final String RECIPE_QUEUE = "recipe_queue_img";
    public static final String IMG_QUEUE = "img_queue";
    public static final String RECIPE_ROUTING_KEY_CREATE = "recipe_routingKey_create_img";
    public static final String RECIPE_ROUTING_KEY_UPDATE = "recipe_routingKey_update_img";
    public static final String IMG_ROUTING_KEY = "img_routingKey";
    public static final String FANOUT_IMG_QUEUE = "fanout_img_queue";

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(IMG_EXCHANGE);
    }

    @Bean
    public Queue img_queue() {
        return new Queue(IMG_QUEUE);
    }

    @Bean
    public Binding bindingImgQueue(Queue img_queue, DirectExchange exchange) {
        return BindingBuilder.bind(img_queue)
                .to(exchange)
                .with(IMG_ROUTING_KEY);
    }

    @Bean
    public Queue recipe_queue_img() {
        return new Queue(RECIPE_QUEUE);
    }

    @Bean
    public Queue fanoutImg() {
        return new Queue(FANOUT_IMG_QUEUE);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
