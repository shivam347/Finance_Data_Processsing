package com.zorvyn.dashboard.dto.user;

import java.time.LocalDateTime;

import com.zorvyn.dashboard.entity.User;
import com.zorvyn.dashboard.enums.Role;
import com.zorvyn.dashboard.enums.UserStatus;

import lombok.Builder;
import lombok.Data;

/* This method is used when user want to fetch their own profile , also used in the frontend
to know the current logged in user , then what to display on the ui based on role, this is the just format */

@Data
@Builder
public class UserResponse {

    private String id;
    private String name;
    private String password;
    private Role role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserResponse fromEntity(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .password(user.getPassword())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

    }

}
