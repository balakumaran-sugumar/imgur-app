package com.syf.imgurapp.imgurapp.controller;

import com.syf.imgurapp.imgurapp.exception.ImageAppException;
import com.syf.imgurapp.imgurapp.model.UserDTO;
import com.syf.imgurapp.imgurapp.model.UserRegistrationResponse;
import com.syf.imgurapp.imgurapp.repository.ImageRepository;
import com.syf.imgurapp.imgurapp.repository.UserRepository;
import com.syf.imgurapp.imgurapp.repository.entity.Image;
import com.syf.imgurapp.imgurapp.repository.entity.User;
import com.syf.imgurapp.imgurapp.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;


    //DO NOT COMMIT
    private final ImageRepository imageRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(@Valid @RequestBody UserDTO user)
            throws ImageAppException {
        return new ResponseEntity<>(userService.register(user), HttpStatus.OK);
    }

    @GetMapping
    public String insertRecords(){
        User user = User.builder()
                .userName("user2")
                .email("temp@temp.com")
                .firstName("Balakumaran")
                .lastName("Sugumar")
                .password(passwordEncoder.encode("password"))
                .build();
        userRepository.save(user);
        IntStream.range(1, 10)
                .forEach(number -> {
                    Image image = Image.builder()
                            .user(user)
                            .url("https://i.imgur.com/FD0pbhR.jpeg")
                            .build();
                    imageRepository.save(image);
                });

        return "success";
    }
}
