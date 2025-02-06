package org.example.rabbit.consumer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
public class BasicListener implements MessageListener {

    @Override
    public void onMessage(Message message) {

        log.info("received message from {}", message.getMessageProperties().getConsumerQueue());
        var bodyAsString = Arrays.toString(message.getBody());
        log.info("body {}", bodyAsString);
    }
}
