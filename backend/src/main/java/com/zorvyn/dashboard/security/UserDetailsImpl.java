package com.zorvyn.dashboard.security;

import java.util.Collection;
import java.util.List;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.zorvyn.dashboard.entity.User;
import com.zorvyn.dashboard.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    @Getter
    private final User user;

    /* This method is returning list.of but inside our system we have only one role of one user , but inside
    spring security it expects one user can have multiple roles so we have to return it as a List.of */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       /* user.getRole() gives you Role.Admin , enum type not string type , simple
       granted authority want string so .name() will convert to string so ROLE_ADMIN */
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {

       return user.getPassword();   

    }

    @Override
    public String getUsername() {
       return user.getEmail();
    }

     @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.ACTIVE;
    }
    
}
