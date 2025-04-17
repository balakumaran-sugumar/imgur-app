package com.syf.imgurapp.transformer;

import com.syf.imgurapp.model.UserDTO;

import com.syf.imgurapp.repository.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class UserInfoTransformerTest {

    @InjectMocks
    private UserInfoTransformer userInfoTransformer;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void transformUserInfoShouldTransformAllFieldsCorrectly() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");
        userDTO.setPassword("plainPassword");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        User result = userInfoTransformer.transformUserInfo(userDTO);
        assertNotNull(result);
        assertEquals("testuser", result.getUserName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertEquals("encodedPassword", result.getPassword());
    }

    @Test
    void transformUserInfoShouldHandleEmptyFields() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("");
        userDTO.setEmail("");
        userDTO.setFirstName("");
        userDTO.setLastName("");
        userDTO.setPassword("");

        when(passwordEncoder.encode("")).thenReturn("encodedEmpty");
        User result = userInfoTransformer.transformUserInfo(userDTO);

        assertNotNull(result);
        assertEquals("", result.getUserName());
        assertEquals("", result.getEmail());
        assertEquals("", result.getFirstName());
        assertEquals("", result.getLastName());
        assertEquals("encodedEmpty", result.getPassword());
    }

    @Test
    void transformUserInfoShouldEncodePassword() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("plainPassword");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        User result = userInfoTransformer.transformUserInfo(userDTO);
        assertEquals("encodedPassword", result.getPassword());
    }

    @Test
    void transformUserInfoShouldHandleNullInput() {
        assertThrows(NullPointerException.class, () -> userInfoTransformer.transformUserInfo(null));
    }

}
