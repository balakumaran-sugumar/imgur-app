package com.syf.imgurapp.imgurapp.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ImageResponse {

    private String message;

    private String id;

    private LocalDateTime dateTime;

}
