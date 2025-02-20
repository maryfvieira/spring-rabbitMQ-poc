package org.example.rabbitmq.producer.processor;

import org.example.rabbitmq.producer.domain.Product;
import org.example.rabbitmq.producer.dto.ProductDTO;
import org.example.rabbitmq.producer.exceptions.BrandNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.Objects;

public class ProductItemProcessor implements ItemProcessor<ProductDTO, Product> {

	private static final Logger log = LoggerFactory.getLogger(ProductItemProcessor.class);

	private static final String ACTIVE_STATUS = "ACTIVE";

	private static final String ADMIN_USER = "admin";

	/**
	 business rules here
	 */
	@Override
	public Product process(ProductDTO productDTO) throws Exception {
		if (Objects.nonNull(productDTO)) {
			log.debug("Processing data for ProductDTO: {}", productDTO);
			if (productDTO.productBrand() == null || productDTO.productBrand().isEmpty()) {
				throw new BrandNotFoundException("Brand not found for product: " + productDTO.productName());
			}

			return new Product(productDTO.productId(), productDTO.productName(), productDTO.productBrand(),
				productDTO.price(), productDTO.description(),
				ADMIN_USER, java.time.LocalDateTime.now(), ACTIVE_STATUS);
		} else {
			log.error("Error: While processing data: ProductDTO is null");
			return null;
		}
	}
}
