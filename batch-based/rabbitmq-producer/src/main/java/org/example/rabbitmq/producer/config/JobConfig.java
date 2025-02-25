package org.example.rabbitmq.producer.config;

import lombok.val;
import org.example.rabbitmq.producer.ProductCsvItemReader;
import org.example.rabbitmq.producer.ProductCsvItemWriter;
import org.example.rabbitmq.producer.partitioner.ProductDataPartitioner;
import org.example.rabbitmq.producer.domain.Product;
import org.example.rabbitmq.producer.dto.ProductDTO;
import org.example.rabbitmq.producer.exceptions.ExceptionSkipPolicy;
import org.example.rabbitmq.producer.listener.ProductImportJobCompletionListener;
import org.example.rabbitmq.producer.listener.StepSkipListener;
import org.example.rabbitmq.producer.processor.ProductItemProcessor;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.amqp.AmqpItemWriter;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class JobConfig {

	private static final String JOB_NAME = "productImportJob";

	@Value("product_catalog_mini.csv")
	private Resource productsCsv;

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public ItemReader<ProductDTO> productItemReader() throws IOException {

		var productCsvItemReader =  new ProductCsvItemReader("product_catalog_mini.csv");
		return productCsvItemReader.getReader();
	}

	/**
	 * Process the product data
	 *
	 * @return ItemProcessor<ProductDTO, Product>
	 */
	@Bean
	public ItemProcessor<ProductDTO, Product> productItemProcessor() {
		return new ProductItemProcessor();
	}

	/**
	 * Partition the product data
	 *
	 * @return ProductDataPartitioner
	 */
	@Bean
	public ProductDataPartitioner productDataPartitioner() {
		return new ProductDataPartitioner();
	}

	/**
	 * Handle the partitioned data
	 *
	 * @param jobRepository JobRepository
	 * @return PartitionHandler
	 */
	@Bean
	public PartitionHandler partitionHandler(JobRepository jobRepository,  ItemReader<ProductDTO> productReader) {
		TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
		taskExecutorPartitionHandler.setGridSize(3);
		taskExecutorPartitionHandler.setTaskExecutor(taskExecutor());
		taskExecutorPartitionHandler.setStep(slaveStep(jobRepository, productReader));
		return taskExecutorPartitionHandler;
	}

	/**
	 * Create the slave step to process the product data
	 *
	 * @param jobRepository JobRepository
	 * @return Step
	 */
	@Bean
	public Step slaveStep(JobRepository jobRepository, ItemReader<ProductDTO> productReader) {
		return new StepBuilder("slaveStep", jobRepository)
			.<ProductDTO, Product>chunk(5, transactionManager())
			.reader(productReader)
			.processor(productItemProcessor())
			.writer(productItemWriter())
			.faultTolerant()
			.listener(stepSkipListener())
			.skipPolicy(new ExceptionSkipPolicy())
			.build();
	}

	/**
	 * Create the master step to partition the product data
	 *
	 * @param jobRepository JobRepository
	 * @return Step
	 */
	@Bean
	public Step masterStep(JobRepository jobRepository, ItemReader<ProductDTO> productReader) {
		return new StepBuilder("masterSTep", jobRepository)
			.partitioner(slaveStep(jobRepository, productReader).getName(), productDataPartitioner())
			.partitionHandler(partitionHandler(jobRepository, productReader))
			.build();
	}

	/**
	 * Write the product data to the database
	 *
	 * @return ProductItemWriter
	 */
//    @Bean
//    public ProductItemWriter productItemWriter() {
//        return new ProductItemWriter();
//    }

	@Bean
	public ProductCsvItemWriter productItemWriter(){
		var exchange = "DIRECT-EXCHANGE-BASIC";
		var routingKey = "TO-FIRST-QUEUE";

		return new ProductCsvItemWriter(exchange, routingKey);
	}

	/**
	 * Create the job to import the product data
	 *
	 * @param jobRepository JobRepository
	 * @param listener      ProductImportJobCompletionListener
	 * @return Job
	 */
	@Bean("productImportJob")
	public Job productImportJob(JobRepository jobRepository, ProductImportJobCompletionListener listener, ItemReader<ProductDTO> productReader) {
		return new JobBuilder(JOB_NAME, jobRepository)
			.listener(listener)
			.start(masterStep(jobRepository, productReader))
			.build();
	}


	/**
	 * Create the task executor
	 *
	 * @return TaskExecutor
	 */
	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(4);
		taskExecutor.setCorePoolSize(4);
		taskExecutor.setQueueCapacity(4);
		return taskExecutor;
	}

	/**
	 * Create the transaction manager
	 *
	 * @return PlatformTransactionManager
	 */
	@Bean
	public PlatformTransactionManager transactionManager() {
		return new JpaTransactionManager();
	}

	/**
	 * Create the step skip listener
	 *
	 * @return StepSkipListener
	 */
	@Bean
	public StepSkipListener stepSkipListener() {
		return new StepSkipListener();
	}

}
