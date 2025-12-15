package com.example.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final Key SECRET_KEY= Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long Expiration=1000L*60*60*24*365;

    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+Expiration))
                .signWith(SECRET_KEY,SignatureAlgorithm.HS256)
                .compact();
    }
    public String extractUsername(String token){
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

        }catch(JwtException e){
            return null;
        }

    }
    public boolean validateToken(String token,String username){
        String extracted=extractUsername(token);
        return (extracted!=null && extracted.equals(username) && !isTokenExpired(token));
    }
    public boolean isTokenExpired(String token){
        try{
            Date expiration= Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build().parseClaimsJws(token).getBody()
                    .getExpiration();
            return expiration.before(new Date());
        }catch(JwtException e){
            return true;
        }
    }
}
