package com.syf.imgurapp.listener;

import com.syf.imgurapp.model.UserImageUploadEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaListener {

    @org.springframework.kafka.annotation.KafkaListener(topics = "imgur-events", groupId = "imgur-app-clients")
    public void listen(UserImageUploadEventDTO message) {
        //The message will be dropped for this iteration
        log.info("Received Message: userName {} imageName {}", message.getUserName(), message.getImageName());
    }

}
