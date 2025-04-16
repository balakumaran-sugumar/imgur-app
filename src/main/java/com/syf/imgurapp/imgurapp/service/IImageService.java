package com.syf.imgurapp.imgurapp.service;

import com.syf.imgurapp.imgurapp.exception.ImageAppException;
import com.syf.imgurapp.imgurapp.model.ImageResponse;
import com.syf.imgurapp.imgurapp.repository.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface IImageService {

    ImageResponse uploadImage(MultipartFile file, UserDetails userDetails) throws ImageAppException;

    User userImageInformation(String username);

    ImageResponse deleteImage(Long imageId, String username);

}
