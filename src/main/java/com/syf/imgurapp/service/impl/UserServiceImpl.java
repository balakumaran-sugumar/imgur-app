package com.syf.imgurapp.service.impl;

import com.syf.imgurapp.exception.ImageAppException;
import com.syf.imgurapp.exception.UserAlreadyExistsException;
import com.syf.imgurapp.model.UserDTO;
import com.syf.imgurapp.model.UserRegistrationResponse;
import com.syf.imgurapp.repository.UserRepository;
import com.syf.imgurapp.repository.entity.User;
import com.syf.imgurapp.service.IUserService;
import com.syf.imgurapp.transformer.UserInfoTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    private final UserInfoTransformer userInfoTransformer;

    @Override
    public UserRegistrationResponse register(UserDTO user) throws ImageAppException {

        //check if user already exists
        Optional<User> dbUser = userRepository.findByUserName(user.getUsername());
        if(dbUser.isPresent()){
            log.error("User already exists : " + user.getUsername());
            throw new UserAlreadyExistsException("User " + user.getUsername() +" Already exists");
        }

        //proceeding to register the user
        User userDtls = userInfoTransformer.transformUserInfo(user);

        //TODO:: resiliency to be implemented (will check)
        User userResp = userRepository.save(userDtls);

        return UserRegistrationResponse.builder()
                .userId(userResp.getUserId()).message("User was successfully registered")
                .build();
    }
}
