package org.example.rabbit.producer.service;

import org.example.rabbit.common.model.Animal;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducerService {

    @Autowired
    AmqpTemplate rabbitTemplate;

    @Value("${spring.topic-exchange.name}")
    private String exchange;

    public void sendToRabbitMQ(Animal message, String routingKey) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
