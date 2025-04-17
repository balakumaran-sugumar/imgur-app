package com.syf.imgurapp.controller;

import com.syf.imgurapp.exception.ImageAppException;
import com.syf.imgurapp.model.UserDTO;
import com.syf.imgurapp.model.UserRegistrationResponse;
import com.syf.imgurapp.repository.entity.User;
import com.syf.imgurapp.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(@Valid @RequestBody UserDTO user)
            throws ImageAppException {
        Instant startTime = Instant.now();
        UserRegistrationResponse userRegResp = userService.register(user);
        log.info("User registration was successful for userName {} totalTime taken {}", user.getUsername(),
                Duration.between(startTime, Instant.now()));
        return new ResponseEntity<>(userRegResp, HttpStatus.OK);
    }

}
