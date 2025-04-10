package com.test.springbatch.config;

import com.test.springbatch.batch.ItemReaderAlex;
import com.test.springbatch.batch.ItemProcessorAlex;
import com.test.springbatch.dao.Hike;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.kafka.KafkaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final ItemReaderAlex itemReaderAlex;
    private final ItemProcessorAlex itemProcessorAlex;
    private final KafkaItemWriter<String, Hike> kafkaItemWriter;

    public BatchConfig(ItemReaderAlex itemReaderAlex, ItemProcessorAlex itemProcessorAlex, KafkaItemWriter<String, Hike> kafkaItemWriter) {
        this.itemReaderAlex = itemReaderAlex;
        this.itemProcessorAlex = itemProcessorAlex;
        this.kafkaItemWriter = kafkaItemWriter;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new org.springframework.batch.support.transaction.ResourcelessTransactionManager();
    }

    @Bean
    public Step kafkaWriterStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("kafkaWriterStep", jobRepository)
                .<Hike, Hike>chunk(10, transactionManager)
                .reader(itemReaderAlex)
                .processor(itemProcessorAlex)
                .writer(kafkaItemWriter)
                .build();
    }

    @Bean
    public Job kafkaWriterJob(JobRepository jobRepository, Step kafkaWriterStep) {
        return new org.springframework.batch.core.job.builder.JobBuilder("kafkaWriterJob", jobRepository)
                .start(kafkaWriterStep)
                .build();
    }
}