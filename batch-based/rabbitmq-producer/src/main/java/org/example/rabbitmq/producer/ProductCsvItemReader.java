package org.example.rabbitmq.producer;

import org.example.rabbitmq.producer.dto.ProductDTO;
import org.example.rabbitmq.producer.mapper.ProductFieldSetMapper;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class ProductCsvItemReader implements ItemReader<ProductDTO> {

	private final String filePath;

	public ProductCsvItemReader(String filePath)  {
		this.filePath = filePath;
	}

	public ItemReader<ProductDTO> getReader() throws IOException {
		ClassPathResource resource = new ClassPathResource(this.filePath);

		return new FlatFileItemReaderBuilder<ProductDTO>()
				.name("ProductDTOReader")
				.resource(resource)
				.fieldSetMapper(new ProductFieldSetMapper())
				.linesToSkip(1)
				.recordSeparatorPolicy(new DefaultRecordSeparatorPolicy())
				.lineMapper(new DefaultLineMapper<ProductDTO>() {{
					setLineTokenizer(new DelimitedLineTokenizer() {{
						setNames("productId", "productName", "productBrand", "price", "description");
						setDelimiter(","); // Set the delimiter to comma
						setQuoteCharacter('\"'); // Set the quote character to double quote
						setStrict(false);
					}});
					setFieldSetMapper(new ProductFieldSetMapper());
				}}).strict(false)
				.targetType(ProductDTO.class)
//				.delimited()
//				.names("productId", "productName", "productBrand", "price", "description") // with names("name", "id") the example fails
				.build();
	}

	@Override
	public ProductDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		return this.getReader().read();
	}
}
