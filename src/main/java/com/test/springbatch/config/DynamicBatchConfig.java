package com.test.springbatch.config;

import com.test.springbatch.dao.Hike;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class DynamicBatchConfig {

    private final ItemProcessor<Hike, Hike> itemProcessor;
    private final ItemWriter<Hike> itemWriter;

    public DynamicBatchConfig(ItemProcessor<Hike, Hike> itemProcessor, ItemWriter<Hike> itemWriter) {
        this.itemProcessor = itemProcessor;
        this.itemWriter = itemWriter;
    }

    @Bean
    public Job dynamicJob(JobRepository jobRepository, PlatformTransactionManager transactionManager, List<DataSource> dataSources) throws Exception {
        List<Step> steps = createDynamicSteps(jobRepository, transactionManager, dataSources);

        JobBuilder jobBuilder = new JobBuilder("dynamicJob", jobRepository);
        Step firstStep = steps.get(0);
        steps.remove(0);

        // Start the job with the first step and add the remaining steps dynamically
        SimpleJobBuilder jobFlow = jobBuilder.start(firstStep);
        for (Step step : steps) {
            jobFlow.next(step);
        }

        return jobFlow.build();
    }

    private List<Step> createDynamicSteps(JobRepository jobRepository, PlatformTransactionManager transactionManager, List<DataSource> dataSources) throws Exception {
        List<Step> steps = new ArrayList<>();

        int stepNumber = 1;
        for (DataSource dataSource : dataSources) {
            JdbcPagingItemReader<Hike> reader = new JdbcPagingItemReaderBuilder<Hike>()
                    .dataSource(dataSource)
                    .name("readerForDataSource" + stepNumber)
                    .queryProvider(createQueryProvider(dataSource).getObject())
                    .pageSize(1000) // Fetch 1000 records per page
                    .rowMapper(new BeanPropertyRowMapper<>(Hike.class))
                    .build();

            Step step = new StepBuilder("dynamicStep" + stepNumber, jobRepository)
                    .<Hike, Hike>chunk(10, transactionManager) // Process 10 records per chunk
                    .reader(reader)
                    .processor(itemProcessor)
                    .writer(itemWriter)
                    .build();

            steps.add(step);
            stepNumber++;
        }

        return steps;
    }

    private SqlPagingQueryProviderFactoryBean createQueryProvider(DataSource dataSource) {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("SELECT *");
        queryProvider.setFromClause("FROM hikes");
        queryProvider.setSortKey("id");
        return queryProvider;
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4); // Number of threads for parallel execution
        taskExecutor.setMaxPoolSize(8);
        taskExecutor.setQueueCapacity(10);
        taskExecutor.initialize();
        return taskExecutor;
    }
}