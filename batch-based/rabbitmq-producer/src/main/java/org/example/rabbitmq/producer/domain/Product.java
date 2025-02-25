package org.example.rabbitmq.producer.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@Builder
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(name = "product_id", unique = true, nullable = true)
	private Long productId;

	@Column(name = "name", nullable = true)
	private String name;

	@Column(name = "brand_name")
	private String brandName;

	@Column(name = "price", nullable = true)
	private double price;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "created_by", nullable = true)
	private String createdBy;

	@Column(name = "created_on", nullable = true)
	private LocalDateTime createdDate;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_on")
	private LocalDateTime updatedDate;

	@Column(name = "status", nullable = true)
	private String status;

	public Product(Long productId, String name, String brandName, double price, String description, String createdBy, LocalDateTime createdDate, String status) {
		this.productId = productId;
		this.name = name;
		this.brandName = brandName;
		this.price = price;
		this.description = description;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.status = status;
	}

}
