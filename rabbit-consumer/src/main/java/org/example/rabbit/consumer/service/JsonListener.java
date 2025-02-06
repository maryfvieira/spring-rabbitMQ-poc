package org.example.rabbit.consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.rabbit.common.model.Customer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Getter
@Service
@Slf4j
public class JsonListener {

    @RabbitListener(queues = "JSON-QUEUE-BASIC")
    public void messageListenerToJsonQueue(String message){
        try {
            var customer = new ObjectMapper().readValue(message, Customer.class);
            log.info("received message from JSON-QUEUE-BASIC");
            log.info("body {}", message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
