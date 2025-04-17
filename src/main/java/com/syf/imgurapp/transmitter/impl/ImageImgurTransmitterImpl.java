package com.syf.imgurapp.transmitter.impl;

import com.syf.imgurapp.exception.ImageAppException;
import com.syf.imgurapp.transmitter.IImageImgurTransmitter;
import com.syf.imgurapp.transmitter.UrlConnectionProvider;
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

    private final UrlConnectionProvider urlConnectionProvider;

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
        try (InputStream inputStream = urlConnectionProvider.openStream(imgUrl);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        } catch (IOException urlException) {
            throw new ImageAppException("Not able to retrieve image from imgur");
        }
    }
}
