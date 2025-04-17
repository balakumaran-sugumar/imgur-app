package com.syf.imgurapp.service;

import com.syf.imgurapp.exception.ImageAppException;
import com.syf.imgurapp.model.ImageDownloadDTO;
import com.syf.imgurapp.model.ImageResponse;
import com.syf.imgurapp.repository.ImageRepository;
import com.syf.imgurapp.repository.UserRepository;
import com.syf.imgurapp.repository.entity.Image;
import com.syf.imgurapp.repository.entity.User;
import com.syf.imgurapp.service.impl.ImageServiceImpl;
import com.syf.imgurapp.transmitter.IImageImgurTransmitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ImageServiceImplTest {

    @InjectMocks
    private ImageServiceImpl imageService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private IImageImgurTransmitter transmitter;

    private UserDetails userDetails;
    private com.syf.imgurapp.repository.entity.User testUser;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp(){

        testUser = User.builder()
                .userName("testuser").build();

        userDetails = org.springframework.security.core.userdetails.User.withUsername("testuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        mockFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
    }

    @Test
    void uploadImageIOExceptionThrowsImageAppException() throws Exception {
        when(transmitter.uploadImage(any())).thenThrow(IOException.class);
        assertThrows(ImageAppException.class, () -> {
            imageService.uploadImage(mockFile, userDetails);
        });
    }
    @Test
    void uploadImageSuccess() throws Exception {
        Map<String, Object> imgurResponse = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("deletehash", "deletehash");
        data.put("link", "http://imgur.com/test.jpg");
        data.put("id", "img123");
        imgurResponse.put("data", data);

        when(transmitter.uploadImage(any())).thenReturn(imgurResponse);
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(testUser));
        when(imageRepository.save(any(Image.class))).thenReturn(new Image());

        ImageResponse response = imageService.uploadImage(mockFile, userDetails);

        assertNotNull(response);
        assertEquals("Image was successfully uploaded to imgur", response.getMessage());
        verify(transmitter).uploadImage(any());
        verify(imageRepository).save(any(Image.class));
    }

    @Test
    void userImageInformationSuccess() {
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(testUser));
        User result = imageService.userImageInformation("testuser");
        assertNotNull(result);
        assertEquals("testuser", result.getUserName());
        verify(userRepository).findByUserName(anyString());
    }

    @Test
    void deleteImageSuccess() {
        Image testImage = new Image();
        testImage.setDeleteHash("abc123");

        when(imageRepository.findById(anyLong())).thenReturn(Optional.of(testImage));

        ImageResponse response = imageService.deleteImage(1L, "testuser");

        assertNotNull(response);
        assertTrue(response.getMessage().contains("was deleted"));
        verify(transmitter).deleteImage(anyString());
        verify(imageRepository).delete(any(Image.class));
    }

    @Test
    void downloadImageSuccess() throws Exception {
        Image testImage = new Image();
        testImage.setUrl("http://imgur.com/test.jpg");
        byte[] testData = "test image data".getBytes();
        when(imageRepository.findById(anyLong())).thenReturn(Optional.of(testImage));
        when(transmitter.downloadImage(anyString())).thenReturn(testData);
        ImageDownloadDTO result = imageService.downloadImage(1L, "testuser");
        assertNotNull(result);
        assertArrayEquals(testData, result.getImageData());
    }

}
