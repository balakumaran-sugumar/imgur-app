package com.syf.imgurapp.kafka;

import com.syf.imgurapp.config.KafkaProducer;
import com.syf.imgurapp.model.UserImageUploadEventDTO;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "imgur-events" }, brokerProperties = {
        "listeners=PLAINTEXT://localhost:9092", "port=9092"
})
public class UserImageEventProducerTest {
    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Test
    void shouldSendImageUploadEvent() throws Exception {
        String username = "testuser";
        String imageName = "profile.jpg";

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafka);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        // Create consumer factory
        ConsumerFactory<String, UserImageUploadEventDTO> factory = new DefaultKafkaConsumerFactory<>(
                consumerProps,
                new StringDeserializer(),
                new JsonDeserializer<>(UserImageUploadEventDTO.class)
        );

        Consumer<String, UserImageUploadEventDTO> consumer = factory.createConsumer();
        consumer.subscribe(Collections.singleton("imgur-events"));

        kafkaProducer.sendMessage(username, imageName);

        ConsumerRecords<String, UserImageUploadEventDTO> records = consumer.poll(Duration.ofSeconds(10));
        assertFalse(records.isEmpty());

        ConsumerRecord<String, UserImageUploadEventDTO> record = records.iterator().next();
        assertEquals(username, record.value().getUserName());
        assertEquals(imageName, record.value().getImageName());

        consumer.close();
    }


}
