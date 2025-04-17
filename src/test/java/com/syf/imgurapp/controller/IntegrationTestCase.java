package com.syf.imgurapp.controller;

import com.syf.imgurapp.repository.ImageRepository;
import com.syf.imgurapp.repository.UserRepository;
import com.syf.imgurapp.repository.entity.User;
import com.syf.imgurapp.transmitter.IImageImgurTransmitter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTestCase {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IImageImgurTransmitter transmitter;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ImageRepository imageRepository;

    @Test
    @WithMockUser(username = "testuser")
    void uploadImageSuccess() throws Exception {
        // 1. Setup test user
        User testUser = new User();
        testUser.setUserName("testuser");
        testUser.setUserId(1L);

        // 2. Mock repository responses
        when(userRepository.findByUserName("testuser"))
                .thenReturn(Optional.of(testUser));

        // 3. Mock Imgur response
        Map<String, Object> mockResponse = Map.of(
                "data", Map.of(
                        "deletehash", "abc123",
                        "link", "http://imgur.com/test.jpg",
                        "id", "img123"
                )
        );
        when(transmitter.uploadImage(any())).thenReturn(mockResponse);

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Test content".getBytes()
        );

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/media/upload")
                        .file(mockFile)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }
}
