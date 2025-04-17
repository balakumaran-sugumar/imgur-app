package com.syf.imgurapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syf.imgurapp.model.UserDTO;
import com.syf.imgurapp.model.UserRegistrationResponse;
import com.syf.imgurapp.repository.ImageRepository;
import com.syf.imgurapp.repository.UserRepository;
import com.syf.imgurapp.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService; // Mocking the IUserService

    @MockBean
    private ImageRepository imageRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private final UserDTO userDTO = UserDTO.builder()
            .username("userName")
            .email("temp@temp.com")
            .firstName("Balakumaran")
            .lastName("Sugumar")
            .password("password").build();

    private final UserRegistrationResponse registrationResponse = UserRegistrationResponse.builder()
            .message("Registered").build();

    @WithMockUser(username = "testUser")
    @Test
    void testRegisterUser() throws Exception {
        when(userService.register(any(UserDTO.class))).thenReturn(registrationResponse);
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDTO))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registered"));
    }


    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }





}
