package org.example.rabbitmq.producer;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.core.io.ClassPathResource;


public abstract class CsvItemReader<T> implements ItemReader<T> {

	public final FlatFileItemReader<T> reader;

	public CsvItemReader(String filePath)  {
		this.reader = new FlatFileItemReader();
		reader.setResource(new ClassPathResource(filePath));
	}

	public void close()  {
		reader.close();
	}
}
