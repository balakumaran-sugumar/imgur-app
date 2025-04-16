package com.syf.imgurapp.imgurapp.model;

import com.syf.imgurapp.imgurapp.repository.entity.Image;
import com.syf.imgurapp.imgurapp.repository.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserImageInfo {

    private User userDetails;

}
