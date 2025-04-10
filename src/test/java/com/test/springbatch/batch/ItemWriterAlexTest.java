package com.test.springbatch.batch;

import com.test.springbatch.dao.Hike;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ItemWriterAlexTest {

    @Test
    void testWrite() throws Exception {
        // Mock KafkaTemplate
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, Hike> kafkaTemplate = (KafkaTemplate<String, Hike>) Mockito.mock(KafkaTemplate.class);

        // Create the ItemWriterAlex instance with the mocked KafkaTemplate
        ItemWriterAlex itemWriterAlex = new ItemWriterAlex(kafkaTemplate);

        // Prepare test data
        Hike hike1 = new Hike(1L, "Hike 1", "alias1", "category1", "2025-01-01", "John Doe", null, null, null, null, null, null, "Place 1", "City 1", "Country 1", "Address 1", 0.0, 0.0, "Short description 1", "Long description 1", "Easy", "Beginner", "Massif 1", 1000, 500, "1234");
        Hike hike2 = new Hike(2L, "Hike 2", "alias2", "category2", "2025-01-02", "Jane Doe", null, null, null, null, null, null, "Place 2", "City 2", "Country 2", "Address 2", 0.0, 0.0, "Short description 2", "Long description 2", "Medium", "Intermediate", "Massif 2", 2000, 1000, "5678");

        List<Hike> hikes = List.of(hike1, hike2);

        // Call the write method
        itemWriterAlex.write(new org.springframework.batch.item.Chunk<>(hikes));

        // Capture the arguments passed to the KafkaTemplate
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Hike> valueCaptor = ArgumentCaptor.forClass(Hike.class);

        verify(kafkaTemplate, times(2)).send(topicCaptor.capture(), keyCaptor.capture(), valueCaptor.capture());

        // Verify the topic
        assertEquals("hike-topic", topicCaptor.getAllValues().get(0));
        assertEquals("hike-topic", topicCaptor.getAllValues().get(1));

        // Verify the keys
        assertEquals("hike-1", keyCaptor.getAllValues().get(0));
        assertEquals("hike-2", keyCaptor.getAllValues().get(1));

        // Verify the values
        assertEquals(hike1, valueCaptor.getAllValues().get(0));
        assertEquals(hike2, valueCaptor.getAllValues().get(1));
    }
}