package com.codingshuttle.projects.airBnbApp.service.impl;

import com.codingshuttle.projects.airBnbApp.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value(value = "${jwt.secret}")
    private String secKey;

    private Long accessTime=600000l;

    private Long refreshTime=60000*131400l;

    private SecretKey key(){
        return Keys.hmacShaKeyFor(
                secKey.getBytes(StandardCharsets.UTF_8)
        );
    }


    public String generateAccessToken(User user) {
        String token= Jwts.builder()
                .subject(user.getUsername())
                .claim("role",user.getRoles())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+accessTime))
                .signWith(key())
                .compact();
        return token;
    }

    public String generateRefreshToken(User user) {
        String token= Jwts.builder()
                .subject(user.getUsername())
                .claim("role",user.getRoles())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+refreshTime))
                .signWith(key())
                .compact();
        return token;
    }


    public String extractUsername(String token) {
        Claims claim=Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claim.getSubject();
    }

}
