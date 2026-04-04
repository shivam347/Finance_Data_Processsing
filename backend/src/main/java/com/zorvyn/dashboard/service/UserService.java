package com.zorvyn.dashboard.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.zorvyn.dashboard.dto.user.UpdateUserRequest;
import com.zorvyn.dashboard.dto.user.UserResponse;
import com.zorvyn.dashboard.repository.UserRepository;
import com.zorvyn.dashboard.entity.User;
import com.zorvyn.dashboard.enums.Role;
import com.zorvyn.dashboard.enums.UserStatus;
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

    /* These all methods are accessible by the admin only */

    /*
     * Method to get all the users but we want only some records on the page not all
     * at the same time
     */
    public Page<UserResponse> getAllUsers(Pageable pageable) {

        return userRepository.findAll(pageable).map(UserResponse::fromEntity);
    }

    /* Method to get user by id */
    public UserResponse getUserById(String id) {
        // through email i will fetch the user from users table
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "user not found"));

        return UserResponse.fromEntity(user);
    }

    /*
     * Method to update the role and status by admin , my logic is admin cannot
     * change its role or status
     * and also admin should choose only between viewer and analyst
     */
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        // First we fetch the user from the db
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "user not found"));

        // Admin should not update it's own profile
        if (user.getRole() == Role.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "cannot update admin profile");
        }

        // Cannot assign the admin role, admin should be one only , this avoid null
        // pointer exception
        if (Role.ADMIN.equals(request.getRole())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "cannot assign admin role");
        }

        // now set the user with role assigned by the admin
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        /* Now set the user with status assigned by the admin */
        if (request.getClass() != null) {
            user.setStatus(request.getStatus());
        }

        /* save the user also */
        return UserResponse.fromEntity(userRepository.save(user));

    }

    /* Method to deactive the account , admin cannot deactivate its own account, */
    public void deactivateAccount(String id) {

        // First fetch the user from the db
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "user not found"));

        /* to implement admin cannot deactive its own account , we do comparison with loggedinUser email and 
        user email , if both are same then they cannot deactive the account, For viewer this is not accessible
        so there is no need to check for viewer  */

        String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if(user.getEmail().equals(loggedInUserEmail)){
            throw new ApiException(HttpStatus.FORBIDDEN, "admin cannot deactivate its own account");
        }

        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

}
