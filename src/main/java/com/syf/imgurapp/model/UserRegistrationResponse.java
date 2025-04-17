package com.syf.imgurapp.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegistrationResponse {

    private Long userId;

    private String message;

}
