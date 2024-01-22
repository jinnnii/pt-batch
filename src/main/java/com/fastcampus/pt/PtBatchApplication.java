package com.fastcampus.pt;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@EnableBatchProcessing
@SpringBootApplication
public class PtBatchApplication {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;


	public PtBatchApplication(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
		this.jobRepository = jobRepository;
		this.platformTransactionManager = platformTransactionManager;
	}

	@Bean
	public Tasklet myTasklet(){
		return (contribution, chunkContext) -> {
			System.out.println("Execute Step");
			return RepeatStatus.FINISHED;
		};
	}
	@Bean
	public Step myStep(JobRepository repository, Tasklet myTasklet, PlatformTransactionManager transactionManager){
		return new StepBuilder("myStep", repository)
				.tasklet(myTasklet, transactionManager)
				.build();
	}

	@Bean
	public Job myJob(JobRepository repository, Step step){
		return new JobBuilder("myJob", repository)
				.start(myStep(repository, myTasklet(), this.platformTransactionManager))
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(PtBatchApplication.class, args);
	}

}
