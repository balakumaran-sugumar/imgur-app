package com.syf.imgurapp.service.impl;

import com.syf.imgurapp.config.KafkaProducer;
import com.syf.imgurapp.exception.ImageAppException;
import com.syf.imgurapp.model.ImageDownloadDTO;
import com.syf.imgurapp.model.ImageResponse;
import com.syf.imgurapp.repository.ImageRepository;
import com.syf.imgurapp.repository.UserRepository;
import com.syf.imgurapp.repository.entity.Image;
import com.syf.imgurapp.repository.entity.User;
import com.syf.imgurapp.service.IImageService;
import com.syf.imgurapp.transmitter.IImageImgurTransmitter;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements IImageService {

    private final IImageImgurTransmitter transmitter;

    private final UserRepository userRepository;

    private final ImageRepository imageRepository;

    private final KafkaProducer kafkaProducer;

    ExecutorService executor = Executors.newFixedThreadPool(5);

    @PreDestroy
    public void preDestroy(){
        if(null != executor){
            executor.shutdown();
        }
    }

    @Override
    public ImageResponse uploadImage(MultipartFile file,
                                     UserDetails userDetails ) throws ImageAppException {
        try {
            Map<String, Object> imgurResponse = transmitter.uploadImage(file);
            log.info("Response from imgur : " + imgurResponse.toString());

            User user = userRepository.findByUserName(userDetails.getUsername()).orElseThrow();
            log.debug("Retrieved user details for upload image : user {}", user.getUserName());

            //storing the information into the image table
            @SuppressWarnings("unchecked") //as the structure in unknown from imgur
            Map<String, Object> imgurData = (Map<String, Object>)imgurResponse.get("data");

            imageRepository.save(getImageDTO(imgurData, user));
            log.info("Successfully uploaded image for user {}", user.getUserName());

            //asynchronously send message kafka
            CompletableFuture.runAsync(() -> kafkaProducer.sendMessage(file.getName(), userDetails.getUsername())
                    , executor);
        }catch (IOException ioException){
            log.error("Could not upload the image to imgur : ex ", ioException);
            throw new ImageAppException("Not able to upload image to imgur");
        }
        return ImageResponse.builder()
                .message("Image was successfully uploaded to imgur")
                .dateTime(LocalDateTime.now())
                .build();
    }

    @Override
    public User userImageInformation(String username) throws ImageAppException {
        log.info("Calling database to get details, cache miss here");
        User user;
        try {
             user = userRepository.findByUserName(username).orElseThrow();
        }catch (Exception ex){
            throw new ImageAppException("Could not fetch details from database reason: " + ex.getMessage());
        }
        log.info("Successfully retrieved the data from user repository");
        return user;
    }

    @Override
    public ImageResponse deleteImage(Long imageId, String username) throws ImageAppException {
        //get the file details
        try {
            Image imageDtls = imageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("Could not find image"));
            log.debug("Fetched image details from database for imageId {}", imageId);

            //delete from imgur
            transmitter.deleteImage(imageDtls.getDeleteHash());
            log.debug("deleted data on Imgur for imageId {}", imageId);

            //delete from database as well
            imageRepository.delete(imageDtls);
            log.info("deleted data for ImageId {} user {}", imageId, username);
        }catch (Exception ex){
            log.error("Not able to delete image reason {}", ex.getMessage());
            throw new ImageAppException("Not able to delete image {}" + ex.getMessage());
        }
        return ImageResponse.builder()
                .id(String.valueOf(imageId))
                .dateTime(LocalDateTime.now())
                .message(imageId + " was deleted")
                .build();
    }

    @Override
    public ImageDownloadDTO downloadImage(Long imageId, String username) throws ImageAppException {
        //get the image from the image entity
        Image image = imageRepository.findById(imageId).orElseThrow();

        byte[] imageBytes = transmitter.downloadImage(image.getUrl());

        return ImageDownloadDTO.builder().imageData(imageBytes).build();
    }

    private Image getImageDTO(Map<String, Object> imgurData, User user){
       return Image.builder()
                .deleteHash((String)imgurData.get("deletehash"))
                .url((String)imgurData.get("link"))
                .imgurId((String)imgurData.get("id"))
                .user(user)
                .build();
    }
}
