package org.example.rabbitmq.producer.config;

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
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.amqp.AmqpItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class JobConfig {

	private static final String JOB_NAME = "productImportJob";

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

/*
	@Bean
	public Step masterStep(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("masterStep")
			.partitioner(slaveStep().getName(), partitioner()) // Define o particionamento
			.step(slaveStep())
			.gridSize(4) // Número de partições paralelas
			.build();
	}
*/

	@Bean
	public Step masterStep(JobRepository jobRepository,
						   PlatformTransactionManager transactionManager,
						   AmqpItemWriter<Product> amqpItemWriter) {
		return new StepBuilder("masterSTep", jobRepository).
			partitioner(slaveStep(jobRepository, transactionManager, amqpItemWriter).getName(), productDataPartitioner())
//			.step(slaveStep(jobRepository, transactionManager, amqpItemWriter))
//			.gridSize(4)
			.partitionHandler(partitionHandler(jobRepository, transactionManager, amqpItemWriter))
			.build();
	}


	@Bean
	public ItemProcessor<ProductDTO, Product> productItemProcessor() {
		return new ProductItemProcessor();
	}

	@Bean
    public ItemReader<ProductDTO> productItemReader() {
		return new ProductCsvItemReader("data/products_catalog_mini.csv");
    }

	@Bean
	public ProductDataPartitioner productDataPartitioner() {
		return new ProductDataPartitioner();
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(4);
		taskExecutor.setCorePoolSize(4);
		taskExecutor.setQueueCapacity(4);
		return taskExecutor;
	}

	@Bean
	public PartitionHandler partitionHandler(JobRepository jobRepository,
											 PlatformTransactionManager transactionManager,
											 AmqpItemWriter<Product> amqpWriter) {
		TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
		taskExecutorPartitionHandler.setGridSize(3);
		taskExecutorPartitionHandler.setTaskExecutor(taskExecutor());
		taskExecutorPartitionHandler.setStep(slaveStep(jobRepository, transactionManager, amqpWriter));
		return taskExecutorPartitionHandler;
	}

	@Bean
	public ItemWriter<Product> amqpWriter(){
		var exchange = "DIRECT-EXCHANGE-BASIC";
		var routingKey = "TO-SECOND-QUEUE";

		return new ProductCsvItemWriter(exchange, routingKey);
	}

	@Bean
	public StepSkipListener stepSkipListener() {
		return new StepSkipListener();
	}


	@Bean
	public Step slaveStep(JobRepository jobRepository,
						  PlatformTransactionManager transactionManager,
						  AmqpItemWriter<Product> amqpItemWriter) {
		return new StepBuilder("slaveStep", jobRepository)
			.<ProductDTO, Product>chunk(10, transactionManager)
			.reader(productItemReader())
			.processor(productItemProcessor())
			.writer(amqpWriter())
			.faultTolerant()
			.listener(stepSkipListener())
			.skipPolicy(new ExceptionSkipPolicy())
			.build();
	}

	@Bean("productImportJob")
	public Job productImportJob(JobRepository jobRepository,
								PlatformTransactionManager transactionManager,
								AmqpItemWriter<Product> amqpItemWriter,
								ProductImportJobCompletionListener listener) {
		return new JobBuilder(JOB_NAME, jobRepository)
			.listener(listener)
			.start(masterStep(jobRepository, transactionManager, amqpItemWriter))
			.build();
	}

}
