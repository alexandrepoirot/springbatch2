package com.test.springbatch.batch;

import com.test.springbatch.config.DataSourceContextHolder;
import com.test.springbatch.dao.Hike;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ItemReaderAlex implements ItemReader<Hike> {
    private List<Hike> hikes = Arrays.asList(
        new Hike(1L, "Hike 1", "alias1", "category1", "2025-01-01", "John Doe", null, null, null, null, null, null, "Place 1", "City 1", "Country 1", "Address 1", 0.0, 0.0, "Short description 1", "Long description 1", "Easy", "Beginner", "Massif 1", 1000, 500, "1234"),
        new Hike(2L, "Hike 2", "alias2", "category2", "2025-01-02", "Jane Doe", null, null, null, null, null, null, "Place 2", "City 2", "Country 2", "Address 2", 0.0, 0.0, "Short description 2", "Long description 2", "Medium", "Intermediate", "Massif 2", 2000, 1000, "5678")
    );
    private int currentIndex = 0;

    public void switchToPrimaryDataSource() {
        DataSourceContextHolder.setDataSourceKey("primary");
    }

    public void switchToSecondaryDataSource() {
        DataSourceContextHolder.setDataSourceKey("secondary");
    }

    @Override
    public Hike read() throws Exception {
        if (currentIndex < hikes.size()) {
            return hikes.get(currentIndex++);
        } else {
            return null; // End of the list
        }
    }
}
