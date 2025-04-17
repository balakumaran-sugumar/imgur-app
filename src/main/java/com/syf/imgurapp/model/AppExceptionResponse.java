package com.syf.imgurapp.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AppExceptionResponse {

    private String message;

    private LocalDateTime timestamp;
}
