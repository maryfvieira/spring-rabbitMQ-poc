package org.example.rabbitmq.producer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.LocalDateTime;

@Slf4j
@EnableScheduling
@Configuration
//@ConditionalOnProperty(name = "schedule.enabled", matchIfMissing = false)
public class ScheduleConfig {

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private Job job;

	@Autowired
	ThreadPoolTaskExecutor poolTaskExecutor;

	@Autowired
	JobLauncher jobLauncher;

	@Bean
	public JobLauncher jobLoader(ThreadPoolTaskExecutor poolTaskExecutor) {
		TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
		jobLauncher.setTaskExecutor(poolTaskExecutor);
//		jobLauncher.setJobRepository(jobRepository);
		return jobLauncher;
	}

	@Bean
	public ThreadPoolTaskExecutor poolTaskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(5);
		threadPoolTaskExecutor.setMaxPoolSize(5);
		threadPoolTaskExecutor.setQueueCapacity(5);
		threadPoolTaskExecutor.setBeanName("ThreadBatch-JOB-");
		return threadPoolTaskExecutor;
	}

	@Scheduled(cron = "${schedule.cron.job}")
	private void runJob() {
		log.info("Starting JOB {}", job.getName());
		poolTaskExecutor.execute(() -> {
			JobParameters jobParameter = new JobParametersBuilder().addString("DateTime", LocalDateTime.now().toString()).toJobParameters();
			try {
				jobLauncher.run(job, jobParameter);
			} catch (Exception e) {
				log.error("An error has occurred during the execution of JOB {}", job.getName());
			}
		});

	}


}
