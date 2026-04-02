package com.zorvyn.dashboard.entity;

import java.time.LocalDateTime;

import com.zorvyn.dashboard.enums.Role;
import com.zorvyn.dashboard.enums.UserStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    private String id;

    private String name;

    private String email;

    private String password;


    private Role role = Role.VIEWER;

    private UserStatus status = UserStatus.ACTIVE;

    
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    
}
