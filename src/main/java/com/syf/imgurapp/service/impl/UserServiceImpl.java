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
        User userResp;
        try {
            //check if user already exists
            Optional<User> dbUser = userRepository.findByUserName(user.getUsername());
            if (dbUser.isPresent()) {
                log.error("User already exists : " + user.getUsername());
                throw new UserAlreadyExistsException("User " + user.getUsername() + " Already exists");
            }
            //proceeding to register the user
            User userDtls = userInfoTransformer.transformUserInfo(user);
            //Addition resiliency can be added here using resilience4j and retryable
            userResp = userRepository.save(userDtls);
            log.info("User registration service call was successful for user {}", user.getUsername());
        }catch (ImageAppException ex){
            throw ex;
        }catch (Exception ex){
            log.error("Could not register user UserName {}, reason {}", user.getUsername(), ex.getMessage());
            throw new ImageAppException("User was not registered, retry later");
        }

        return UserRegistrationResponse.builder()
                .userId(userResp.getUserId()).message("User was successfully registered")
                .build();
    }
}
