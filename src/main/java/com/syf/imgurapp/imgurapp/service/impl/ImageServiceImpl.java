package com.syf.imgurapp.imgurapp.service.impl;

import com.syf.imgurapp.imgurapp.exception.ImageAppException;
import com.syf.imgurapp.imgurapp.model.ImageResponse;
import com.syf.imgurapp.imgurapp.repository.ImageRepository;
import com.syf.imgurapp.imgurapp.repository.UserRepository;
import com.syf.imgurapp.imgurapp.repository.entity.Image;
import com.syf.imgurapp.imgurapp.repository.entity.User;
import com.syf.imgurapp.imgurapp.service.IImageService;
import com.syf.imgurapp.imgurapp.transmitter.IImageImgurTransmitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements IImageService {

    private final IImageImgurTransmitter transmitter;

    private final UserRepository userRepository;

    private final ImageRepository imageRepository;
    @Override
    public ImageResponse uploadImage(MultipartFile file,
                                     UserDetails userDetails ) throws ImageAppException {
        try {
            Map<String, Object> imgurResponse = transmitter.uploadImage(file);
            log.info("Response from imgur : " + imgurResponse.toString());

            User user = userRepository.findByUserName(userDetails.getUsername()).orElseThrow();
            //storing the information into the image table
            @SuppressWarnings("unchecked") //as the structure in unknown from imgur
            Map<String, Object> imgurData = (Map<String, Object>)imgurResponse.get("data");

            Image image = Image.builder()
                    .deleteHash((String)imgurData.get("deletehash"))
                    .url((String)imgurData.get("link"))
                    .imgurId((String)imgurData.get("id"))
                    .user(user)
                    .build();

            imageRepository.save(image);
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
    public User userImageInformation(String username) {
        User user = userRepository.findByUserName(username).orElseThrow();
        return user;
    }

    @Override
    public ImageResponse deleteImage(Long imageId, String username) {
        //get the file details
        //throw custom exception
        Image imageDtls = imageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("Could not find image"));
        //delete from imgur
        transmitter.deleteImage(imageDtls.getDeleteHash());

        //delete from database as well
        imageRepository.delete(imageDtls);
        return ImageResponse.builder()
                .id(String.valueOf(imageId))
                .dateTime(LocalDateTime.now())
                .message(imageId + " was deleted")
                .build();
    }
}
