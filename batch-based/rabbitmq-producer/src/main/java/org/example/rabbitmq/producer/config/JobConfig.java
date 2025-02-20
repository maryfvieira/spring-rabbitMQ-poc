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
import org.springframework.beans.factory.annotation.Autowired;
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


@Configuration
@EnableBatchProcessing(dataSourceRef = "dataSource", transactionManagerRef = "transactionManager")
public class JobConfig {

	private static final String JOB_NAME = "productImportJob";

	@Autowired
	private DataSource dataSource;

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Value("data/products_catalog_mini.csv")
	private Resource productsCsv;

	/*@Bean(name = "dataSource")
	@BatchDataSource
	public DataSource H2Datasource() {

		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		EmbeddedDatabase embeddedDatabase = builder
			.addScript("classpath:org/springframework/batch/core/schema-drop h2.sql")
   			.addScript("classpath:org/springframework/batch/core/schema-h2.sql")
   			.setType(EmbeddedDatabaseType.H2)
			.build();
		return embeddedDatabase;

	}*/

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
	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager() {
		return new JpaTransactionManager();
	}

	@Bean
	public JobRepository jobRepository() throws Exception {
		val jobrepositoryFactoryBean = new JobRepositoryFactoryBean();
		jobrepositoryFactoryBean.setDataSource(dataSource);
		jobrepositoryFactoryBean.setTransactionManager(transactionManager());
		jobrepositoryFactoryBean.afterPropertiesSet();
		return jobrepositoryFactoryBean.getObject();
	}

	@Bean
	public Step masterStep(JobRepository jobRepository,
						   PlatformTransactionManager transactionManager) {
		return new StepBuilder("masterSTep", jobRepository).
			partitioner(slaveStep(jobRepository, transactionManager).getName(), productDataPartitioner())
//			.step(slaveStep(jobRepository, transactionManager, amqpItemWriter))
//			.gridSize(4)
			.partitionHandler(partitionHandler(jobRepository, transactionManager))
			.build();
	}

	@Bean
	public ItemProcessor<ProductDTO, Product> productItemProcessor() {
		return new ProductItemProcessor();
	}

	@Bean
    public ItemReader<ProductDTO> productItemReader() {
//		return new ProductCsvItemReader("data/products_catalog_mini.csv");
		return new ProductCsvItemReader(productsCsv);
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
											 PlatformTransactionManager transactionManager
											 ) {
		TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
		taskExecutorPartitionHandler.setGridSize(3);
		taskExecutorPartitionHandler.setTaskExecutor(taskExecutor());
		taskExecutorPartitionHandler.setStep(slaveStep(jobRepository, transactionManager));
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
						  PlatformTransactionManager transactionManager) {
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
								ProductImportJobCompletionListener listener) {
		return new JobBuilder(JOB_NAME, jobRepository)
			.listener(listener)
			.start(masterStep(jobRepository, transactionManager))
			.build();
	}
}
