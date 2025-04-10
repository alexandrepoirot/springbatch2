package com.test.springbatch.batch;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.test.springbatch.dao.Hike;

import java.nio.charset.StandardCharsets;

@Component
public class ItemWriterAlex implements ItemWriter<Hike> {

    private final KafkaTemplate<String, Hike> kafkaTemplate;

    public ItemWriterAlex(KafkaTemplate<String, Hike> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void write(Chunk<? extends Hike> chunk) throws Exception {
        for (Hike hike : chunk) {
            // Create a ProducerRecord with headers
            ProducerRecord<String, Hike> producerRecord = new ProducerRecord<>(
                "hike-topic", // Kafka topic
                "hike-" + hike.getId(), // Message key
                hike // Message value
            );

            // Add headers to the producerRecord
            producerRecord.headers().add(new RecordHeader("source", "ItemWriterAlex".getBytes(StandardCharsets.UTF_8)));
            producerRecord.headers().add(new RecordHeader("processedBy", "ItemProcessorAlex".getBytes(StandardCharsets.UTF_8)));

            // Send the producerRecord to Kafka
            kafkaTemplate.send(producerRecord);
        }
    }
}