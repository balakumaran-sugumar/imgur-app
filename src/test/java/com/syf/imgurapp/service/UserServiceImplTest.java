package com.syf.imgurapp.service;

import com.syf.imgurapp.repository.UserRepository;
import com.syf.imgurapp.service.impl.UserServiceImpl;
import com.syf.imgurapp.transformer.UserInfoTransformer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserInfoTransformer userInfoTransformer;


}
