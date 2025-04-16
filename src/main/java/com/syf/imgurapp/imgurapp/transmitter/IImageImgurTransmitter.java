package com.syf.imgurapp.imgurapp.transmitter;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface IImageImgurTransmitter {

    Map<String, Object> uploadImage(MultipartFile file) throws IOException;

    Map<String, Object> getImage(String deleteHash);

    public void deleteImage(String deleteHash);

}
