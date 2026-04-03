package com.zorvyn.dashboard.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zorvyn.dashboard.dto.auth.AuthResponse;
import com.zorvyn.dashboard.dto.auth.LoginRequest;
import com.zorvyn.dashboard.dto.auth.RegisterRequest;
import com.zorvyn.dashboard.entity.User;
import com.zorvyn.dashboard.enums.Role;
import com.zorvyn.dashboard.enums.UserStatus;
import com.zorvyn.dashboard.exception.ApiException;
import com.zorvyn.dashboard.repository.UserRepository;
import com.zorvyn.dashboard.security.JwtUtil;
import com.zorvyn.dashboard.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor /* Generates constructor only for final fields and @NotNull fields */
public class AuthService {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder; // used for comparing hashed password

    private final AuthenticationManager authenticationManager; // Used to authenticate user details in spring security

    /* Method for register  and generating token while registering only because we don't want our user to login again , directly he/she can enter into the application */
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email is already in use");
        }

        // Now create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.VIEWER); // By default role is viewer
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        // Now i need to generate token with the credentials
        String token = jwtUtil.generateToken(new UserDetailsImpl(user));

        return new AuthResponse(token);

    }

    /* Method for Login */
    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // now get the object
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Now check the user is is inactive or not

        if (userDetails.getUser().getStatus() == UserStatus.INACTIVE) {
            throw new ApiException(HttpStatus.FORBIDDEN, "User account is inactive");
        }

        // Now generate token with the credentials
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(token);

    }

}
