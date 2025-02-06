package org.example.rabbit.producer.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.rabbit.common.model.Customer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/person")
@Slf4j
public class CustomerController {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("{exchange}/{routingKey}")
    public HttpEntity<?> postOnExchange(@PathVariable String exchange, @PathVariable String routingKey, @RequestBody String message){
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        log.debug("post message {} to exchange {} in routingKey {}", message, exchange, routingKey);
        return ResponseEntity.ok().build();
    }

    @PostMapping("json/{exchange}/{routingKey}")
    public HttpEntity<?> postJsonExchange(@PathVariable String exchange, @PathVariable String routingKey, @RequestBody Customer message){
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        log.debug("post json message {} to exchange {} in routingKey {}", message, exchange, routingKey);
        return ResponseEntity.ok().build();
    }

}
