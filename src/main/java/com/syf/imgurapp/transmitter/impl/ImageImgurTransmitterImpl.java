package com.syf.imgurapp.transmitter.impl;

import com.syf.imgurapp.exception.ImageAppException;
import com.syf.imgurapp.transmitter.IImageImgurTransmitter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageImgurTransmitterImpl implements IImageImgurTransmitter {

    private final WebClient webClient;

    @Override
    public Map<java.lang.String,java.lang.Object> uploadImage(MultipartFile file) throws IOException {

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", file.getBytes());

        return webClient.post()
                .uri("/image")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(body))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    @Override
    public Map<String, Object> getImage(String deleteHash){
        return webClient.get()
                .uri("/image/" + deleteHash)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    @Override
    public void deleteImage(String deleteHash){
        webClient.delete()
                .uri("/image/" + deleteHash)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    @Override
    public byte[] downloadImage(String imgUrl) throws ImageAppException {
        try {
            URI uri = URI.create(imgUrl);
            URL url = uri.toURL();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            try (InputStream inputStream = connection.getInputStream();
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                //restricting to 8kb of data
                byte[] buffer = new byte[8192];
                while ((inputStream.read(buffer)) != -1) {
                    baos.write(buffer);
                }
                return baos.toByteArray();
            }
        }catch (IOException urlException){
            //log.error("Caught in execution while retrieving the image from Imgur ex: {}", urlException.getMessage());
            throw new ImageAppException("Not able to retrieve image from imgur, try again later");
        }
    }
}
