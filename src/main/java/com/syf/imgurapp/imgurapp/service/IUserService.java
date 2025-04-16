package com.syf.imgurapp.imgurapp.service;

import com.syf.imgurapp.imgurapp.exception.ImageAppException;
import com.syf.imgurapp.imgurapp.model.UserDTO;
import com.syf.imgurapp.imgurapp.model.UserRegistrationResponse;

public interface IUserService {
    UserRegistrationResponse register(UserDTO user) throws ImageAppException;

}
