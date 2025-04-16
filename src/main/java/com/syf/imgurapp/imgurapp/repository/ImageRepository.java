package com.syf.imgurapp.imgurapp.repository;

import com.syf.imgurapp.imgurapp.repository.entity.Image;
import com.syf.imgurapp.imgurapp.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

}
