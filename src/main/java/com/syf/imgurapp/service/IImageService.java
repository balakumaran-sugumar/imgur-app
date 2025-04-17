package com.syf.imgurapp.service;

import com.syf.imgurapp.exception.ImageAppException;
import com.syf.imgurapp.model.ImageDownloadDTO;
import com.syf.imgurapp.model.ImageResponse;
import com.syf.imgurapp.repository.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface IImageService {

    ImageResponse uploadImage(MultipartFile file, UserDetails userDetails) throws ImageAppException;

    User userImageInformation(String username) throws ImageAppException;

    ImageResponse deleteImage(Long imageId, String username) throws ImageAppException;

    ImageDownloadDTO downloadImage(Long imageId, String username) throws ImageAppException;

}
