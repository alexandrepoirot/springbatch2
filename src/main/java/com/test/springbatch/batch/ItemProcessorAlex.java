package com.test.springbatch.batch;

import com.test.springbatch.dao.Hike;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ItemProcessorAlex implements ItemProcessor<Hike, Hike> {
    @Override
    public Hike process(Hike hike) throws Exception {
        // Example: Add "Processed" to the title
        hike.setTitre("Processed: " + hike.getTitre());
        return hike;
    }
}
