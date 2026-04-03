package com.zorvyn.dashboard.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/* dto heps us to send only the requirred fields which are necessary not all fields of the user entity */

@Data
public class RegisterRequest {

    @NotBlank(message = "name is requirred")
    private String name;

    @NotBlank(message = "email is requirred")
    @Email(message = "Invalid email format")
    private String email;


    @NotBlank(message = "password is requirred")
    private String password;
    
}
