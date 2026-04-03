package com.zorvyn.dashboard.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "email is requirred")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "password is requirred")
    private String password;
    
}
