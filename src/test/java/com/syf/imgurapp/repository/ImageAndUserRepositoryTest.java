package com.syf.imgurapp.repository;

import com.syf.imgurapp.repository.entity.Image;
import com.syf.imgurapp.repository.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ImageAndUserRepositoryTest {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindImage() {
        User user = User.builder()
                .userName("testuser")
                .email("test@test.com")
                .firstName("Test")
                .lastName("LastName")
                .password("password")
                .build();
        user = userRepository.save(user);

        Image image = Image.builder()
                .url("http://imgur.com/test.jpg")
                .deleteHash("hashDelete")
                .user(user)
                .build();

        Image savedImage = imageRepository.save(image);
        assertNotNull(savedImage.getImageId());

        Optional<Image> foundImage = imageRepository.findById(savedImage.getImageId());
        assertTrue(foundImage.isPresent());
        assertEquals("http://imgur.com/test.jpg", foundImage.get().getUrl());
    }


}
