package com.test.springbatch.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.batch.item.kafka.KafkaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.test.springbatch.dao.Hike;

@Configuration
public class KafkaWriterConfig {

    @Bean
    public KafkaItemWriter<String, Hike> kafkaItemWriter() {
        KafkaTemplate<String, Hike> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        KafkaItemWriter<String, Hike> writer = new KafkaItemWriter<>();
        writer.setKafkaTemplate(kafkaTemplate);
        writer.setItemKeyMapper(hike -> "hike-" + hike.getId()); // Optional: Customize the key

        // Add headers to each message
        // Headers can be added using interceptors or by customizing the KafkaTemplate if needed.
        // Removed setHeaderMapper as it is not defined for KafkaItemWriter.

        return writer;
    }

    @Bean
    public ProducerFactory<String, Hike> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // Use JSON serializer
        return new DefaultKafkaProducerFactory<>(configProps);
    }
}