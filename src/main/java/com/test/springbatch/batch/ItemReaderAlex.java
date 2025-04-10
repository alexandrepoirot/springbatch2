package com.test.springbatch.batch;

import com.test.springbatch.dao.Hike;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class ItemReaderAlex implements ItemReader<Hike> {

    private final JdbcPagingItemReader<Hike> primaryReader;

    public ItemReaderAlex(DataSource primaryDataSource) throws Exception {
        // Configure the primary reader with paging
        this.primaryReader = new JdbcPagingItemReaderBuilder<Hike>()
                .dataSource(primaryDataSource)
                .name("primaryPagingReader")
                .queryProvider(createQueryProvider(primaryDataSource).getObject())
                .pageSize(1000) // Fetch 1000 records per page
                .rowMapper(new BeanPropertyRowMapper<>(Hike.class))
                .build();

        // Open the reader (Spring Batch will manage the lifecycle in production)
        this.primaryReader.open(null);
    }

    @Override
    public Hike read() throws Exception {
        // Delegate reading to the primary reader
        return primaryReader.read();
    }

    private SqlPagingQueryProviderFactoryBean createQueryProvider(DataSource dataSource) {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("SELECT *");
        queryProvider.setFromClause("FROM hikes");
        queryProvider.setSortKey("id"); // Sort by the primary key or another indexed column
        return queryProvider;
    }
}
