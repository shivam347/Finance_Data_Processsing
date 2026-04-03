package com.zorvyn.dashboard.service;

import java.net.http.HttpResponse;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.zorvyn.dashboard.dto.user.UserResponse;
import com.zorvyn.dashboard.repository.UserRepository;
import com.zorvyn.dashboard.entity.User;
import com.zorvyn.dashboard.exception.ApiException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    // Dependency injection
    private final UserRepository userRepository;

    /* Method called as getUserInfo and return type is UserResponse */
    public UserResponse getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        return UserResponse.fromEntity(user);

    }

}
