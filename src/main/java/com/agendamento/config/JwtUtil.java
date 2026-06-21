package com.agendamento.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private Key getkey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    public String gerarToken(String Email) {
        return Jwts.builder()
                .setSubject(Email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getkey())
                .compact();
    }
    public String extrairEmail(String Token) {
        return Jwts.parserBuilder()
                .setSigningKey(getkey())
                .build()
                .parseClaimsJws(Token)
                .getBody()
                .getSubject();
    }
    public boolean tokenValido(String Token) {
        try {
            extrairEmail(Token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    }


