package com.syf.imgurapp.model;

import com.syf.imgurapp.repository.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserImageInfo {

    private User userDetails;

}
