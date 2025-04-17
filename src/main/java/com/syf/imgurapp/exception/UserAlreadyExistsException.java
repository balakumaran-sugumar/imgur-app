package com.syf.imgurapp.exception;

public class UserAlreadyExistsException extends ImageAppException{
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
