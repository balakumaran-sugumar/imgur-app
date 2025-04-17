package com.syf.imgurapp.transmitter;

import com.syf.imgurapp.exception.ImageAppException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface IImageImgurTransmitter {

    Map<String, Object> uploadImage(MultipartFile file) throws IOException;

    Map<String, Object> getImage(String deleteHash);

    void deleteImage(String deleteHash);

    byte[] downloadImage(String imgUrl) throws ImageAppException;

}
