package com.syf.imgurapp.controller;

import com.syf.imgurapp.exception.ImageAppException;
import com.syf.imgurapp.model.UserDTO;
import com.syf.imgurapp.model.UserRegistrationResponse;
import com.syf.imgurapp.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(@Valid @RequestBody UserDTO user)
            throws ImageAppException {
        return new ResponseEntity<>(userService.register(user), HttpStatus.OK);
    }

}
