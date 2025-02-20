package org.example.rabbitmq.producer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.rabbitmq.producer.domain.Product;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.batch.item.amqp.AmqpItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;

@Configuration("RabbitConfig")
public class RabbitConfig {

	@Autowired
	private MessageConverter jsonMessageConverter;

	@Bean
	public ConnectionFactory connectionFactory() {
		return new CachingConnectionFactory("localhost");
	}

	@Bean("firstQueue")
	public Queue firstQueue(){
		return QueueBuilder
			.durable("FIRST-QUEUE-BASIC")
			.build();
	}

	@Bean
	public Exchange directExchange(){
		return ExchangeBuilder
			.directExchange("DIRECT-EXCHANGE-BASIC")
			.durable(true)
			.build();
	}

	@Bean
	public Binding firstDirectBinding(){
		return BindingBuilder
			.bind(firstQueue())
			.to(directExchange())
			.with("TO-FIRST-QUEUE")
			.noargs();
	}

	@Bean
	public AmqpAdmin amqpAdmin() {
		return new RabbitAdmin(connectionFactory());
	}

	@Bean
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());

//		rabbitTemplate.setRoutingKey(queue().getName());
//		rabbitTemplate.setMessageConverter(jsonMessageConverter);
		return rabbitTemplate;
	}

//	@Bean
//	public AmqpItemWriter<Product> amqpWriter(){
//		AmqpItemWriter<Product> amqpItemWriter = new AmqpItemWriter<>(this.rabbitTemplate());
//		return amqpItemWriter;
//	}
}

