package com.zorvyn.dashboard.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zorvyn.dashboard.dto.user.UpdateUserRequest;
import com.zorvyn.dashboard.dto.user.UserResponse;
import com.zorvyn.dashboard.service.UserService;
import com.zorvyn.dashboard.utils.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Users" , description = "Endpoints for managing users (FOR ADMIN ONLY)")
public class UserController {

    private final UserService userService;

    /* Controller to get all records and also get pagenated*/
    @GetMapping
    @Operation(summary = "Get all the Records(Paginated)")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(Pageable pageable){
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(pageable)));
        
    }


    /* Controller to get specific user by id */
    @GetMapping("/{id}")
    @Operation(summary = "Get User by Id")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String id){
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }


    /* Controller to update the user role and status */
    @PutMapping("/{id}")
    @Operation(summary = "Endpoints to update user Role and Status (ADMIN ONLY)")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable String id , @Valid @RequestBody UpdateUserRequest request){

        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(id, request), "user updated successfully"));

    }

    /* Controller to deactive the user */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deactive the User (ONLY FOR ADMIN)")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable String id){
        userService.deactivateAccount(id);

        return ResponseEntity.ok(ApiResponse.success(null, "Account deactivated successfully"));
    }







    
}
