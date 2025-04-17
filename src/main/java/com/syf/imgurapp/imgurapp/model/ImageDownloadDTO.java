package com.syf.imgurapp.imgurapp.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageDownloadDTO {

    private String message;

    private byte[] imageData;
}
