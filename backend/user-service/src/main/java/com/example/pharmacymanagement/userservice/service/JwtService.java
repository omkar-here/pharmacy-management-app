package com.example.pharmacymanagement.userservice.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.pharmacymanagement.userservice.entity.EmployeeRole;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
// @PropertySource("classpath:application.properties")
public class JwtService {
    // @Value("${auth.secret}")
    private static String AUTH_SECRET = "9a4f2c8d3b7a1e6f45c8a0b3f267d8b1d4e6f3c8a9d2b5f8e3a9c8b5f6v8a3d9";
    // @Value("${refresh.secret}")
    private static String REFRESH_SECRET = "9a4f2c8d3b7a1e6f75c8a0b3f267d8b1d4e6f3c8a9d2b5f8e3a9c8b5f6v8a3d9";
    // @Value("${auth.validity}")
    public static int AUTH_VALIDITY = 15;
    // @Value("${refresh.validity}")
    public static int REFRESH_VALIDITY = 30;

    public String createAuthToken(String username, Integer id, EmployeeRole role) {
        Map<String, String> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("id", id.toString());
        claims.put("role", role.toString());
        return generateToken(claims, false);
    }

    public String createRefreshToken(Integer id) {
        Map<String, String> claims = new HashMap<>();
        claims.put("id", id.toString());
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

    public Map<String, String> getClaimsFromToken(String token, Boolean isRefreshToken) {
        Map<String, Object> claims = Jwts.parserBuilder()
                .setSigningKey(
                        Keys.hmacShaKeyFor(Decoders.BASE64.decode(isRefreshToken ? REFRESH_SECRET : AUTH_SECRET)))
                .build()
                .parseClaimsJws(token)
                .getBody();
        Map<String, String> claimsMap = new HashMap<>();
        claims.forEach((String key, Object value) -> claimsMap.put(key, value.toString()));
        return claimsMap;
    }

    private String generateToken(Map<String, String> claims, Boolean isRefreshToken) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(isRefreshToken ? new Date(System.currentTimeMillis() + 1000 * REFRESH_VALIDITY)
                        : new Date(System.currentTimeMillis() + 1000 * AUTH_VALIDITY))
                .signWith(
                        Keys.hmacShaKeyFor(Decoders.BASE64.decode(isRefreshToken ? REFRESH_SECRET : AUTH_SECRET)),
                        SignatureAlgorithm.HS256)
                .compact();
    }
}
