package org.example.rabbitmq.producer;

import org.example.rabbitmq.producer.dto.ProductDTO;
import org.example.rabbitmq.producer.mapper.ProductFieldSetMapper;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.ClassPathResource;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;

public class ProductCsvItemReader extends CsvItemReader<ProductDTO> {

	public ProductCsvItemReader(String filePath) {
		super(filePath);
	}

	@Override
	public ProductDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		reader.setLinesToSkip(1); // Skip the header line
		reader.setRecordSeparatorPolicy(new DefaultRecordSeparatorPolicy());
		reader.setLineMapper(new DefaultLineMapper<ProductDTO>() {{
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setNames("productId", "productName", "productBrand", "price", "description");
				setDelimiter(","); // Set the delimiter to comma
				setQuoteCharacter('\"'); // Set the quote character to double quote
				setStrict(false);
			}});
			setFieldSetMapper(new ProductFieldSetMapper());
		}});

		return reader.read();

	}
}
