package org.example.rabbitmq.producer.dto;
import lombok.Builder;

@Builder
public record ProductDTO(Long productId,
						 String productName,
						 String productBrand,
						 double price,
						 String description) { }
