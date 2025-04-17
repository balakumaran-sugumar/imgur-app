package com.syf.imgurapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syf.imgurapp.model.ImageDownloadDTO;
import com.syf.imgurapp.model.ImageResponse;
import com.syf.imgurapp.repository.entity.User;
import com.syf.imgurapp.service.IImageService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageController.class)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IImageService imageService;

    @MockBean
    private UserDetails userDetails;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @WithMockUser(username = "testUser")
    @Test
    void testUploadImage() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.jpg",
                MediaType.IMAGE_JPEG_VALUE, "dummy content".getBytes());
        ImageResponse mockResponse = ImageResponse.builder().message("Uploaded").build();
        when(imageService.uploadImage(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(mockResponse);
        mockMvc.perform(multipart("/api/media/upload")
                        .file(mockFile)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Uploaded"));
    }

    @WithMockUser(username = "testUser")
    @Test
    void testGetUserAssociatedImageList() throws Exception {
        User user = User.builder()
                .userName("testUser")
                .images(Collections.emptyList())
                .build();
        when(imageService.userImageInformation("testUser")).thenReturn(user);
        mockMvc.perform(get("/api/media")
                        .principal(() -> "testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("testUser"));
    }

    @WithMockUser(username = "testUser")
    @Test
    void testDeleteImageDtls() throws Exception {
        ImageResponse mockResponse = ImageResponse.builder().message("Image deleted").build();
        when(imageService.deleteImage(1L, "testUser")).thenReturn(mockResponse);
        mockMvc.perform(delete("/api/media/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Image deleted"));
    }

    @WithMockUser(username = "testUser")
    @Test
    void testDownloadImage() throws Exception {
        byte[] dummyData = "image-bytes".getBytes(StandardCharsets.UTF_8);
        ImageDownloadDTO dto = ImageDownloadDTO.builder().imageData(dummyData).build();
        when(imageService.downloadImage(1L, "testUser")).thenReturn(dto);
        mockMvc.perform(get("/api/media/download/1")
                        .principal(() -> "testUser"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/jpeg"))
                .andExpect(content().bytes(dummyData));
    }
}