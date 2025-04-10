package com.test.springbatch.batch;

import com.test.springbatch.dao.Hike;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class ItemReaderAlex implements ItemReader<Hike> {

    private final JdbcCursorItemReader<Hike> primaryReader;
    private final JdbcCursorItemReader<Hike> secondaryReader;
    private Iterator<Hike> combinedIterator;

    public ItemReaderAlex(DataSource primaryDataSource, DataSource secondaryDataSource) {
        // Configure the primary reader
        this.primaryReader = new JdbcCursorItemReaderBuilder<Hike>()
                .dataSource(primaryDataSource)
                .sql("SELECT * FROM hikes") // Replace with your actual query
                .rowMapper(new BeanPropertyRowMapper<>(Hike.class))
                .name("primaryReader")
                .build();

        // Configure the secondary reader
        this.secondaryReader = new JdbcCursorItemReaderBuilder<Hike>()
                .dataSource(secondaryDataSource)
                .sql("SELECT * FROM hikes") // Replace with your actual query
                .rowMapper(new BeanPropertyRowMapper<>(Hike.class))
                .name("secondaryReader")
                .build();

        // Combine results from both readers
        List<Hike> combinedResults = new ArrayList<>();
        try {
            primaryReader.open(null); // Open the primary reader
            Hike hike;
            while ((hike = primaryReader.read()) != null) {
                combinedResults.add(hike);
            }
            primaryReader.close(); // Close the primary reader

            secondaryReader.open(null); // Open the secondary reader
            while ((hike = secondaryReader.read()) != null) {
                combinedResults.add(hike);
            }
            secondaryReader.close(); // Close the secondary reader
        } catch (Exception e) {
            throw new RuntimeException("Error reading data from data sources", e);
        }

        this.combinedIterator = combinedResults.iterator();
    }

    @Override
    public Hike read() {
        if (combinedIterator.hasNext()) {
            return combinedIterator.next();
        } else {
            return null; // End of the combined list
        }
    }
}
