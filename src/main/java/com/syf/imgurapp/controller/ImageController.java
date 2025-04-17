package com.syf.imgurapp.controller;

import com.syf.imgurapp.exception.ImageAppException;
import com.syf.imgurapp.model.ImageDownloadDTO;
import com.syf.imgurapp.model.ImageResponse;
import com.syf.imgurapp.repository.entity.User;
import com.syf.imgurapp.service.IImageService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Slf4j
public class ImageController {

    private final IImageService imageService;

    //upload image to imgur
    @CacheEvict(value = "imgurCache", key = "#userDetails.username")
    @PostMapping("/upload")
    public ResponseEntity<ImageResponse> uploadImage(@RequestParam("file")MultipartFile file,
                                                     @AuthenticationPrincipal UserDetails userDetails) throws ImageAppException {
       //for file checks only
       if(null == file || file.isEmpty()){
           ImageResponse imageResponse = ImageResponse.builder().message("File Cannot be emty")
                   .dateTime(LocalDateTime.now()).build();
           return ResponseEntity.badRequest().body(imageResponse);
       }
       Instant startTime = Instant.now();
       ImageResponse imageResponse = imageService.uploadImage(file, userDetails);
       log.info("Successfully updated images filename {}, userName {} total time take for uploading the image {}", file.getName(),
               userDetails.getUsername(), Duration.between(startTime, Instant.now()).toMillis());
       return new ResponseEntity<>(imageResponse, HttpStatus.OK);
    }

    @Cacheable(value = "imgurCache", key = "#userDetails.username")
    @GetMapping
    public ResponseEntity<User> getUserAssociatedImageList(@AuthenticationPrincipal UserDetails userDetails) throws ImageAppException {
       Instant startTime = Instant.now();
       User userImageInfo = imageService.userImageInformation(userDetails.getUsername());
       log.info("Successfully retrieved list of images for the user : username {} timeTaken {}", userDetails.getUsername(),
                Duration.between(startTime, Instant.now()).toMillis());
       return new ResponseEntity<>(userImageInfo, HttpStatus.OK);
    }

    @CacheEvict(value = "imgurCache", key = "#userDetails.username")
    @DeleteMapping("/{imageId}")
    public ResponseEntity<ImageResponse> deleteImageDtls(@PathVariable @NotNull Long imageId,
                                                         @AuthenticationPrincipal UserDetails userDetails) throws ImageAppException {
        Instant startTime = Instant.now();
        ImageResponse imageResponse = imageService.deleteImage(imageId, userDetails.getUsername());
        log.info("Successfully deleted images for the user : username {} timeTaken {}", userDetails.getUsername(),
                Duration.between(startTime, Instant.now()).toMillis());
        return new ResponseEntity<>(imageResponse, HttpStatus.OK);
    }

    @Cacheable(value = "imgurDownloadCache")
    @GetMapping("/download/{imageId}")
    public ResponseEntity<byte[]> downloadImage(@AuthenticationPrincipal UserDetails userDetails, @PathVariable
    @NotNull Long imageId) throws ImageAppException {
        Instant startTime = Instant.now();
        ImageDownloadDTO imageDetails = imageService.downloadImage(imageId, userDetails.getUsername());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "image/jpeg");

        log.info("Successfully downloaded images for the user : username {} timeTaken {}", userDetails.getUsername(),
                Duration.between(startTime, Instant.now()).toMillis());

        return ResponseEntity.ok()
                .headers(headers)
                .body(imageDetails.getImageData());
    }
}
