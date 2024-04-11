package com.example.pharmacymanagement.appservice.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.example.pharmacymanagement.appservice.entity.EmployeeRole;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
@PropertySource("classpath:application.properties")
public class JwtService {
    @Value("${auth.secret}")
    private static String AUTH_SECRET;
    @Value("${refresh.secret}")
    private static String REFRESH_SECRET;
    @Value("${auth.validity}")
    private static int AUTH_VALIDITY;
    @Value("${refresh.validity}")
    private static int REFRESH_VALIDITY;

    public String createAuthToken(String username, Integer id, EmployeeRole role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("id", id);
        claims.put("role", role);
        return generateToken(claims, false);
    }

    public String createRefreshToken(Integer id) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        return generateToken(claims, true);
    }

    public boolean validateAuthToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(AUTH_SECRET)))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(REFRESH_SECRET)))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> getClaimsFromToken(String token, Boolean isRefreshToken) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(isRefreshToken ? REFRESH_SECRET : AUTH_SECRET)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String generateToken(Map<String, Object> claims, Boolean isRefreshToken) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(isRefreshToken ? new Date(System.currentTimeMillis() + 1000 * 60 * AUTH_VALIDITY)
                        : new Date(System.currentTimeMillis() + 1000 * 60 * REFRESH_VALIDITY))
                .signWith(
                        Keys.hmacShaKeyFor(Decoders.BASE64.decode(isRefreshToken ? REFRESH_SECRET : AUTH_SECRET)),
                        SignatureAlgorithm.HS256)
                .compact();
    }
}
