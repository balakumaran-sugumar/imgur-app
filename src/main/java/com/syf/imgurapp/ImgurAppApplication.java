package com.syf.imgurapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ImgurAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImgurAppApplication.class, args);
    }

}
