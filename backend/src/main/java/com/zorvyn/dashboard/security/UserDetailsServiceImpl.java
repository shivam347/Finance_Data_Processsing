package com.zorvyn.dashboard.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.zorvyn.dashboard.entity.User;
import com.zorvyn.dashboard.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{

       User user =  userRepository.findByEmail(username)
       .orElseThrow(() -> new UsernameNotFoundException("User not found with email"+ username));

       return new UserDetailsImpl(user);

    }


    
}
