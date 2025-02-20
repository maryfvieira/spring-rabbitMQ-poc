package org.example.rabbitmq.producer.config;

import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {


	@Bean(name = "dataSource")
	public DataSource springDataSource() {
		return new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
			.build();
	}

	/*@Bean(name = "dataSource")
	@BatchDataSource
	public DataSource H2Datasource() {

		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		EmbeddedDatabase embeddedDatabase = builder
			.addScript("classpath:org/springframework/batch/core/schema-drop h2.sql")
   			.addScript("classpath:org/springframework/batch/core/schema-h2.sql")
   			.setType(EmbeddedDatabaseType.H2)
			.build();
		return embeddedDatabase;*/

	}



