package com.syf.imgurapp.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.resources.ConnectionProvider;

import reactor.netty.http.client.HttpClient;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class WebClientConfig {

    @Value("${imgur.client-id}")
    private String clientId;

    //using connection pooling to reuse connection and faster connection retrieval
    ConnectionProvider provider = ConnectionProvider.builder("imgur-connection-pool")
            .maxConnections(50)
            .pendingAcquireTimeout(Duration.ofSeconds(60))
            .build();

    HttpClient httpClient = HttpClient.create(provider)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .doOnConnected(conn ->
                    conn.addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS))
                            .addHandlerLast(new WriteTimeoutHandler(5, TimeUnit.SECONDS))
            );

    @Bean
    public WebClient imgurWebClient(){
        return WebClient.builder()
                .baseUrl("https://api.imgur.com/3")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("Authorization", "Client-ID", clientId)
                .build();
    }


}
