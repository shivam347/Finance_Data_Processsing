package com.zorvyn.dashboard.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import org.springframework.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {


    private final JwtUtil jwtUtil;
    
    private final UserDetailsServiceImpl userDetailService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {

            // First i need to get the token from the request
            String jwt = parseJwt(request);

            /* jwt should not be null &&  user should not be authenticated then only we proceed further  */
            if(jwt != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    String username  =   jwtUtil.extractUsername(jwt);

                    // Now i want to fetch that user from the db 
                UserDetails userDetails =    userDetailService.loadUserByUsername(username);

                // Now check the token is valid or not 
                if(jwtUtil.validateToken(jwt, userDetails)){
                    UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); /* provide null value for password as there is no need for password we verify only token */
                    
                //    adding some extra details which are optionals
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    /* NOW tell the spring security that user is authenticated */
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                
                }              

            }
            
        } catch (Exception e) {

            logger.error("Cannot set user Authentication {}", e);
           
        }

        /* Send the request to controller now */
        filterChain.doFilter(request, response);
    }


    private String parseJwt(HttpServletRequest request){
        String headerAuth = request.getHeader("Authorization");

        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")){

            return headerAuth.substring(7);
        }

        return null;
    }
    
}
