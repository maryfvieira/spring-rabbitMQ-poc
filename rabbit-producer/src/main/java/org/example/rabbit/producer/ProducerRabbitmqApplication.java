package org.example.rabbit.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProducerRabbitmqApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProducerRabbitmqApplication.class, args);
	}

	@Bean
	public MessageConverter messageConverter(ObjectMapper objectMapper)  {

		return new Jackson2JsonMessageConverter(objectMapper);
	}

}
