package org.example.rabbitmq.producer;

import org.example.rabbitmq.producer.domain.Product;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductCsvItemWriter implements ItemWriter<Product> {

	private final String exchange;
	private final String routingKey;

	public ProductCsvItemWriter(String exchange, String routingKey){
		this.exchange = exchange;
		this.routingKey = routingKey;
	}

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Override
	public void write(Chunk<? extends Product> chunk) throws Exception {
		for(Product item: chunk.getItems()){
			amqpTemplate.convertAndSend(exchange, routingKey, item);
		}
	}
}
