package com.syf.imgurapp.transmitter;

import com.syf.imgurapp.transmitter.impl.ImageImgurTransmitterImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageImgurTransmitterTest {

    @InjectMocks
    private ImageImgurTransmitterImpl imageImgurTransmitter;

    @Mock
    private WebClient webClient;

    @Mock(lenient = true)  // Mark as lenient if this might not be used in all tests
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock(lenient = true)
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock(lenient = true)
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock(lenient = true)
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock(lenient = true)
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private UrlConnectionProvider urlConnectionProvider;

    @Test
    void uploadImageSuccess() throws IOException {
        byte[] fileBytes = "test image content".getBytes();
        Map<String, Object> expectedResponse = Collections.singletonMap("success", true);

        when(multipartFile.getBytes()).thenReturn(fileBytes);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        Map<String, Object> result = imageImgurTransmitter.uploadImage(multipartFile);
        assertEquals(expectedResponse, result);
    }

    @Test
    void getImageSuccess() {
        String deleteHash = "testHash";
        Map<String, Object> expectedResponse = Collections.singletonMap("id", "123");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        Map<String, Object> result = imageImgurTransmitter.getImage(deleteHash);
        assertEquals(expectedResponse, result);
    }

    @Test
    void deleteImageSuccess() {
        String deleteHash = "testHash";
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        imageImgurTransmitter.deleteImage(deleteHash);
        verify(responseSpec).toBodilessEntity();
    }
    @Test
    void downloadImageSuccess() throws Exception {
        String imgUrl = "https://i.imgur.com/test.jpg";
        byte[] expectedBytes = "image data".getBytes();

        when(urlConnectionProvider.openStream(imgUrl))
                .thenReturn(new ByteArrayInputStream(expectedBytes));

        byte[] result = imageImgurTransmitter.downloadImage(imgUrl);

        assertEquals(expectedBytes.length, result.length);
        assertArrayEquals(expectedBytes, result);

        byte[] trimmedResult = Arrays.copyOf(result, expectedBytes.length);
        assertArrayEquals(expectedBytes, trimmedResult);
    }

}
