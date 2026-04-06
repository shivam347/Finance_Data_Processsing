package com.zorvyn.dashboard.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.zorvyn.dashboard.security.JwtAuthFilter;
import com.zorvyn.dashboard.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;

@Configuration /* used when we define beans inside this class */
@EnableWebSecurity /* activate the spring security */
@EnableMethodSecurity /* heps us to use annotation like preauthorize for role based control */
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    private final UserDetailsServiceImpl userDetailsService;

    private final PasswordEncoder passwordEncoder;

    // @Value("${app.cors.allowed-origin:http://localhost:5173}")
    // private String allowedOrigin;

    /*
     * When you try to login , spring does not know how to verify the user and where
     * the user is stored and
     * how we verify the password , so we have a method called DaoAuthentication
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService); // must
        authProvider.setPasswordEncoder(passwordEncoder); // must

        return authProvider;
    }

    /* Security filter chain configurations */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers("/api/docs/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**")
                        .permitAll()
                        .anyRequest().authenticated()

                );

        http.authenticationProvider(authenticationProvider()); /*
                                                                * Tell spring security to use my custom authentication
                                                                * logic not the default one
                                                                */

        /* Runs my custom jwtauth filter first then spring default login */
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        // ADD THE FRONTEND ORIGINS HERE
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:4200",
                "https://finance-data-processsing.vercel.app"));
        configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "PATCH", "PUT", "OPTIONS")); /*
                                                                                                       * Options are
                                                                                                       * used by the
                                                                                                       * browser , to
                                                                                                       * check which
                                                                                                       * http methods
                                                                                                       * and headers are
                                                                                                       * allowed by the
                                                                                                       * server, without
                                                                                                       * options
                                                                                                       * Give cors error
                                                                                                       */
        configuration.setAllowedHeaders(List.of("*")); /* important for authorization header */
        configuration.setAllowCredentials(true); /* allowed credentials like cookies, authorization header, sessions */

        /*
         * Now i want to apply all cors configuration to all end points , this is used
         * by spring security internally
         */
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
