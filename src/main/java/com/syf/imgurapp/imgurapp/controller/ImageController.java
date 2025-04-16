package com.syf.imgurapp.imgurapp.controller;

import com.syf.imgurapp.imgurapp.exception.ImageAppException;
import com.syf.imgurapp.imgurapp.model.ImageResponse;
import com.syf.imgurapp.imgurapp.repository.entity.User;
import com.syf.imgurapp.imgurapp.service.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// a repo merge to the main github branch will trigger a webhook on AWS codePipeline - which will pull in the lastest code
// build it and deploy to elasticBeanStalk where the application is accessible

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class ImageController {

    private final IImageService imageService;

    //upload image to imgur
    @PostMapping("/upload")
    public ResponseEntity<ImageResponse> uploadImage(@RequestParam("file")MultipartFile file,
                                                     @AuthenticationPrincipal UserDetails userDetails) throws ImageAppException {
       ImageResponse imageResponse = imageService.uploadImage(file, userDetails);
       return new ResponseEntity<>(imageResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<User> getUserAssociatedImageList(@AuthenticationPrincipal UserDetails userDetails){
       User userImageInfo = imageService.userImageInformation(userDetails.getUsername());
       return new ResponseEntity<>(userImageInfo, HttpStatus.OK);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<ImageResponse> deleteImageDtls(@PathVariable Long imageId, @AuthenticationPrincipal UserDetails userDetails){
        ImageResponse imageResponse = imageService.deleteImage(imageId, userDetails.getUsername());
        return new ResponseEntity<>(imageResponse, HttpStatus.OK);
    }

}
