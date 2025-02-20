package org.example.rabbitmq.producer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Product {

    private Long id;
    private Long productId;
    private String name;
    private String brandName;
    private double price;
    private String description;
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
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
