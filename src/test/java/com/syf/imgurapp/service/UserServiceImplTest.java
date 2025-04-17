package com.syf.imgurapp.service;

import com.syf.imgurapp.exception.ImageAppException;
import com.syf.imgurapp.exception.UserAlreadyExistsException;
import com.syf.imgurapp.model.UserDTO;
import com.syf.imgurapp.model.UserRegistrationResponse;
import com.syf.imgurapp.repository.UserRepository;
import com.syf.imgurapp.repository.entity.User;
import com.syf.imgurapp.service.impl.UserServiceImpl;
import com.syf.imgurapp.transformer.UserInfoTransformer;
import com.syf.imgurapp.transformer.UserInfoTransformerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserInfoTransformer userInfoTransformer;

    private UserDTO testUserDTO;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserName("testuser");
        testUser.setUserId(1L);

        testUserDTO = UserDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .build();

    }
    @Test
    void registerSuccess() throws ImageAppException {
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());
        when(userInfoTransformer.transformUserInfo(any(UserDTO.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserRegistrationResponse response = userService.register(testUserDTO);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("User was successfully registered", response.getMessage());
    }

    @Test
    void registerUserExistsThrowsException() {
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(testUser));

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.register(testUserDTO);
        });
    }

}
