package org.example.rabbitmq.producer.dto;

public record ProductDTO(Long productId,
						 String productName,
						 String productBrand,
						 double price,
						 String description) { }
