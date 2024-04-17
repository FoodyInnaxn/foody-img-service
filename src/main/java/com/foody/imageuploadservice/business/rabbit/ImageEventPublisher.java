package com.foody.imageuploadservice.business.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foody.imageuploadservice.configuration.RabbitMQConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageEventPublisher {
    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void publishImagesEvent(ImageEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.IMG_EXCHANGE, RabbitMQConfig.IMG_ROUTING_KEY, event);
    }

//    private Message createMessageSearch(Object request){
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            MessageProperties messageProperties = new MessageProperties();
//            messageProperties.setContentType("application/json");
//            messageProperties.setContentEncoding("UTF-8");
//            Message msg = new Message(objectMapper.writeValueAsBytes(request), messageProperties);
//            return msg;
//        } catch (JsonProcessingException e) {
//            e.printStackTrace(); // Handle or log the exception as needed
//            return null; // or throw a custom exception
//        }
//    }
}
