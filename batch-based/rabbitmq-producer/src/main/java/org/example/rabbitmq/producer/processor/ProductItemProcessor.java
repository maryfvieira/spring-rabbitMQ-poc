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
	 * Process the provided ProductDTO and convert it to Product
	 *
	 * @param productDTO ProductDTO
	 * @return Product
	 * @throws Exception
	 */
	@Override
	public Product process(ProductDTO productDTO) throws Exception {
		if (Objects.nonNull(productDTO)) {
			log.debug("Processing data for ProductDTO: {}", productDTO);
			if (productDTO.productBrand() == null || productDTO.productBrand().isEmpty()) {
				throw new BrandNotFoundException("Brand not found for product: " + productDTO.productName());
			}

			var product = Product.builder()
				.productId(productDTO.productId())
				.brandName(productDTO.productBrand())
				.name(productDTO.productName())
				.createdBy(ADMIN_USER)
				.createdDate( java.time.LocalDateTime.now())
				.description(productDTO.description())
				.updatedBy(ADMIN_USER)
				.status("OK")
				.price(productDTO.price()).build();


			return product;
		} else {
			log.error("Error: While processing data: ProductDTO is null");
			return null;
		}
	}
}
