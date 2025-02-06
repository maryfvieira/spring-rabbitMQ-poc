package org.example.rabbit.consumer.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.example.rabbit.common.model.Customer;
import org.example.rabbit.consumer.ExceptionLoggingAdvice;
import org.example.rabbit.consumer.service.BasicListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

@Configuration
public class ConsumerConfig {

    @Bean
    public Queue queueBasic(){
        return QueueBuilder.durable("FIRST-QUEUE-BASIC").build();
    }

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private BasicListener basicListener;

    @Autowired
    private SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory;

    @Bean
    public MessageListenerContainer listenerContainerToFirstBasicQueue(){
        var container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(queueBasic());
        container.setMessageListener(basicListener);
        simpleRabbitListenerContainerFactory.setAdviceChain(new ExceptionLoggingAdvice());
        container.setAdviceChain(simpleRabbitListenerContainerFactory.getAdviceChain());

//        simpleRabbitListenerContainerFactory.setMessageConverter(jackson2JsonMessageConverter());

        return container;

    }



}
