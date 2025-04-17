package com.syf.imgurapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WebClientConfig {

    @Value("${imgur.client-id}")
    private String clientId;

    //TODO:: Implement connection pooling
    @Bean
    public WebClient imgurWebClient(){
        return WebClient.builder()
                .baseUrl("https://api.imgur.com/3")
                .defaultHeader("Authorization", "Client-ID", clientId)
                .build();
    }


}
