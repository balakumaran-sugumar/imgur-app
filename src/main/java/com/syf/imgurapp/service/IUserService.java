package com.syf.imgurapp.service;

import com.syf.imgurapp.exception.ImageAppException;
import com.syf.imgurapp.model.UserDTO;
import com.syf.imgurapp.model.UserRegistrationResponse;

public interface IUserService {
    UserRegistrationResponse register(UserDTO user) throws ImageAppException;

}
