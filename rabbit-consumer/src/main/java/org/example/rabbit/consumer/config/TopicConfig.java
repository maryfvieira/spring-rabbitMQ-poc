package org.example.rabbit.consumer.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicConfig {

    @Autowired
    @Qualifier("firstQueue")
    private Queue firstQueue;

    @Autowired
    @Qualifier("secondQueue")
    private Queue secondQueue;

    @Autowired
    @Qualifier("jsonQueue")
    private Queue jsonQueue;

    @Bean
    public Exchange topicExchange(){
        return ExchangeBuilder
                .topicExchange("TOPIC-EXCHANGE-BASIC")
                .durable(true)
                .build();

    }

    @Bean
    public Binding firstTopicBinding(){

        return BindingBuilder
                .bind(firstQueue)
                .to(topicExchange())
                .with("FIRST-TOPIC.#")
                .noargs();
    }

    @Bean
    public Binding secondTopicBinding(){

        return BindingBuilder
                .bind(secondQueue)
                .to(topicExchange())
                .with("FIRST-TOPIC.#")
                .noargs();
    }

    //    @Bean
//    public Binding secondTopicBinding(){
//
//        return BindingBuilder
//                .bind(firstQueue)
//                .to(topicExchange())
//                .with("SECOND-TOPIC.#")
//                .noargs();
//    }
//    @Bean
//    public Binding secondTopicBinding(){
//
//        return BindingBuilder
//                .bind(topicQueue)
//                .to(topicExchange())
//                .with("SECOND-TOPIC.#")
//                .noargs();
//    }
//    @Bean
//    public Binding jsonTopicBinding(){
//
//        return BindingBuilder
//                .bind(jsonQueue)
//                .to(topicExchange())
//                .with("routing.key.#")
//                .noargs();
//    }
}
