package com.syf.imgurapp.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
//Not using @Data - possibility of exposure of data via toString
public class UserDTO {

    @NotBlank(message="User Name cannot be blank")
    @Size(min= 3, max = 20, message = "Username can be between 3 and 20 character long")
    private String username;

    //keeping it simple, other parameter checks like special characters, lower-upper case combination can be added as per
    //need
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message="Password needs to be 6 character long")
    private String password;

    //keeping it simple - Middle Name, Salutation are not considered for simplicity purpose
    @NotBlank(message = "First Name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last Name cannot be blank")
    private String lastName;

    //doing basic validation for email, not checking the integrity with actual emailing systems
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Not a valid email")
    private String email;

    private String requestUUID;

}
