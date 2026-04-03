package com.zorvyn.dashboard.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component /*spring will manage it  */
public class JwtUtil {

    private final Key key;  /*secret key used for token verification */

    private final long jwtExpirationMs;  /* for much time will token be valid */

    public JwtUtil(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-ms}") long jwtExpirationMs){

        this.key = Keys.hmacShaKeyFor(secret.getBytes());  /* convert secret string into secure key */
        this.jwtExpirationMs = jwtExpirationMs;

    }


    /* Method which will extract username from token */
    public String extractUsername(String token){

        return extractClaim(token, Claims::getSubject);
    }

    /* Method which will extract expirations of the token using generic extract method*/
    public Date extractExpiration(String token){

        return extractClaim(token, Claims::getExpiration);
    }


    /* Generic Method to extract any claim from the token
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

       final Claims claims = extractAllClaims(token);
       
        return claimsResolver.apply(claims);
    }


    /* Verify token using key and provide any claims  */
    private Claims extractAllClaims(String token) {
       return Jwts.parserBuilder()
                  .setSigningKey(key)
                  .build()
                  .parseClaimsJws(token)
                  .getBody();
    }


    /* Method to check the token is expired or not */
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }


    /* When the user login in , it will call this method to generate token */
    public String generateToken(UserDetails userDetails){
        // Inside token i want to add the role also which will help me extract role after login
        Map<String, Object> claims = new HashMap<>();

        if(userDetails instanceof UserDetailsImpl){
            claims.put("role", ( (UserDetailsImpl)userDetails).getUser().getRole().name());
        }

        return createToken(claims, userDetails.getUsername());
    }

    /* Used builder method to set the claims , subject , etc , to generate token */
    public String createToken(Map<String, Object> claims, String subject){

        return Jwts.builder()
             .setClaims(claims)
             .setSubject(subject)
             .setIssuedAt(new Date(System.currentTimeMillis()))
             .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
             .signWith(key, SignatureAlgorithm.HS256)
             .compact();
    }


    /* Method which will validate the token */
    public boolean validateToken(String token, UserDetails userDetails){

        final String username  =   extractUsername(token);

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
}
