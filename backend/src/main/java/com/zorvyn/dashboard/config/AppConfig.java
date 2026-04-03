package com.zorvyn.dashboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@Configuration /* Tells spring that this class will define beans */
@RequiredArgsConstructor
public class AppConfig {

    @Bean /* These methods will be registered in the spring container so whenever we need we will use these beans */
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // Verify username and password
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception{

        return authConfig.getAuthenticationManager();
    } 
    
}
