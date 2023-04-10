package com.backendcafe.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.util.function.Function;

@Slf4j
@Service
public class JwtUtil {
    private String secret = "ch+BVQw6MDLCLzgIl/gs+sjMXEFoJ9wBQ5HfNEODiq18wszZLbDakMUaA9HrS84CzTsebAbkZrh7zzZmWC0THw==";

    public String extractUsername(String token) {
        log.info("wtf");
        try {
            return extractClamis(token, Claims::getSubject);
        }catch (Exception exception){

        }
        return null;

    }

    public Date extractExpiration(String token) {
        return extractClamis(token, Claims::getExpiration);
    }

    public <T> T extractClamis(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        log.info("Generate Token by {}", username);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {

        log.info("Create Token by {}", claims);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
