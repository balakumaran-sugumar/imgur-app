package com.syf.imgurapp.config;

import com.syf.imgurapp.model.UserImageUploadEventDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {
    private final KafkaTemplate<String, UserImageUploadEventDTO> kafkaTemplate;

    @Value("${imgur.kafka-topic}")
    private String kafkaTopic;

    public KafkaProducer(KafkaTemplate<String, UserImageUploadEventDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String userName, String imageName) {
        UserImageUploadEventDTO uploadEventDTO = UserImageUploadEventDTO
                .builder()
                .userName(userName)
                .imageName(imageName)
                .build();
        kafkaTemplate.send(kafkaTopic, uploadEventDTO);
    }
}