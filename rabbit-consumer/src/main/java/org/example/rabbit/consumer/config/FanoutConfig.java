package org.example.rabbit.consumer.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FanoutConfig {

    @Autowired
    @Qualifier("firstQueue")
    private Queue firstQueue;

    @Autowired
    @Qualifier("secondQueue")
    private Queue secondQueue;

    @Autowired
    @Qualifier("thirdQueue")
    private Queue thirdQueue;

    @Bean
    public Exchange fanoutExchange(){
        return ExchangeBuilder
                .fanoutExchange("FANOUT-EXCHANGE-BASIC")
                .durable(true)
                .build();
    }

    @Bean
    public Binding FirstfanoutBinding(){
        return BindingBuilder
                .bind(firstQueue)
                .to(fanoutExchange())
                .with("")
                .noargs();
    }
    @Bean
    public Binding SecondfanoutBinding(){
        return BindingBuilder
                .bind(secondQueue)
                .to(fanoutExchange())
                .with("")
                .noargs();
    }
    @Bean
    public Binding ThirdfanoutBinding(){
        return BindingBuilder
                .bind(thirdQueue)
                .to(fanoutExchange())
                .with("")
                .noargs();
    }
}
