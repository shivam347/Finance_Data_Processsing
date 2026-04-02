package com.zorvyn.dashboard.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zorvyn.dashboard.entity.User;

/* Repository layer is the layer which perform operations on database
by default it has some operation like save , find , delete etc */


@Repository
public interface UserRepository extends JpaRepository<User, String>{

    /* Using optional to avoid null exception , used when we want to find user by email */
    Optional<User> findByEmail(String email);


    /* Used during registration when we want to check user already exists by this gmail or not */
    boolean existsByEmail(String email);

    
}
