package com.atlaspay.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret.key}")
    private String jwtSecret;

    @Value("${jwt.expiration.time}")
    private Long jwtExpirationMs;

    private SecretKey getSignkey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     *
     * Generate JWT token for authenticated user
     *
     **/
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSignkey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extract email form jwt token
     */
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSignkey())
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    /**
     * Validate token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(getSignkey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            System.out.println("Invalid JWT token: " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT token: " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            System.out.println("Unsupported JWT token: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT claims string is empty: " + ex.getMessage());
        }
        return  false;
    }

    /**
     * get token expiration time
     */
    public Long getTokenExperationTime() {
        return jwtExpirationMs;
    }
}
