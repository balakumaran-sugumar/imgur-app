package com.syf.imgurapp.imgurapp.transformer;

import com.syf.imgurapp.imgurapp.model.UserDTO;
import com.syf.imgurapp.imgurapp.repository.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInfoTransformer {

    private final PasswordEncoder passwordEncoder;

    public User transformUserInfo(UserDTO userDTO){
        return User.builder()
                .userName(userDTO.getUsername())
                .email(userDTO.getEmail())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .build();
    }

}
