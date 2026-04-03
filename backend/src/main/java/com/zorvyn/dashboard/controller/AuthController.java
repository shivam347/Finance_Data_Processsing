package com.zorvyn.dashboard.controller;

import com.zorvyn.dashboard.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.zorvyn.dashboard.dto.auth.AuthResponse;
import com.zorvyn.dashboard.dto.auth.LoginRequest;
import com.zorvyn.dashboard.dto.auth.RegisterRequest;
import com.zorvyn.dashboard.dto.user.UserResponse;
import com.zorvyn.dashboard.service.AuthService;
import com.zorvyn.dashboard.utils.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor  /* create constructor with final type , so no need for autowired */
@Tag(name = "Authentication", description = "Endpoints for user registrations and login")
public class AuthController {

    private final UserService userService;
    private final AuthService authService; /* Make sure it is final then also it will inject otherwise it will fail */


    /* Api controller for registration  */
    @PostMapping("/register")
    @Operation(summary = "Register a new user , BY default VIEWER IS ROLE")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request){

        return ResponseEntity.ok(ApiResponse.success(authService.register(request), "user registered successfully"));

    }

 /* Api controller for login */
    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get Jwt token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request){

        return ResponseEntity.ok(ApiResponse.success(authService.login(request), "user login successfully"));

    }
    

    /* Api controller for getting current user */
    @GetMapping("/me")
    @Operation(summary = "Get the current logged in user profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication auth){

        return ResponseEntity.ok(ApiResponse.success(userService.getUserInfo(auth.getName())));
        
    }
}
