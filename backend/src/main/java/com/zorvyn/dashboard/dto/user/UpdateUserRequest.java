package com.zorvyn.dashboard.dto.user;

import com.zorvyn.dashboard.enums.Role;
import com.zorvyn.dashboard.enums.UserStatus;

import lombok.Data;

@Data
public class UpdateUserRequest {

    // There are two things which admin which update the role and the status
    private Role role;
    private UserStatus status;
    
}
