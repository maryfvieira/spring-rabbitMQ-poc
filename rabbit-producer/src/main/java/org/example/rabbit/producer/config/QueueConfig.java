package org.example.rabbit.producer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

    @Bean("firstQueue")
    public Queue firstQueue(){
        return QueueBuilder
                .durable("FIRST-QUEUE-BASIC")
                .build();
    }

    @Bean("secondQueue")
    public Queue secondQueue(){
        return QueueBuilder
                .durable("SECOND-QUEUE-BASIC")
                .build();
    }

    @Bean("jsonQueue")
    public Queue jsonQueue(){
        return QueueBuilder
                .durable("JSON-QUEUE-BASIC")
                .build();
    }
    @Bean("thirdQueue")
    public Queue topicQueue(){
        return QueueBuilder
                .durable("THIRD-QUEUE-BASIC")
                .build();
    }
}
