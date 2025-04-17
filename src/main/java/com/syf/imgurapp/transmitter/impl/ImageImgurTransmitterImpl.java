package com.syf.imgurapp.transmitter.impl;

import com.syf.imgurapp.exception.ImageAppException;
import com.syf.imgurapp.transmitter.IImageImgurTransmitter;
import com.syf.imgurapp.transmitter.UrlConnectionProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

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
                .onStatus(HttpStatusCode::isError, this::handleErrorResponse)
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    @Override
    public Map<String, Object> getImage(String deleteHash){
        return webClient.get()
                .uri("/image/" + deleteHash)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleErrorResponse)
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    @Override
    public void deleteImage(String deleteHash){
        webClient.delete()
                .uri("/image/" + deleteHash)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleErrorResponse)
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

    private Mono<Throwable> handleErrorResponse(ClientResponse clientResponse){
        return clientResponse.bodyToMono(String.class)
                .switchIfEmpty(Mono.just("No Error details in the exception"))
                .flatMap(error -> {
                    HttpStatusCode status = clientResponse.statusCode();
                    String errorMessage = "";
                    switch(status){
                        case TOO_MANY_REQUESTS:
                            errorMessage = "Too Many request to Imgur, request could not proceed";
                            break;
                        case BAD_REQUEST:
                            errorMessage = "Bad request 4xx";
                            break;
                        case SERVICE_UNAVAILABLE:
                            errorMessage = "Service is unavailable";
                            break;
                        default:
                            errorMessage = "Error while processing request with imgur";
                    }
                    return Mono.error(new ImageAppException(errorMessage));
                });
    }

}
