package com.wegagenbank.securityservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY="be9e845d7d4c43d8deb5cc37cd893223d225cffce8feea27e3ef5722bbe2f9e0";
    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims =extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ){
        return  Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username= extractUsername(token);
        return  (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return  extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return  extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){

        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }

    public void validateToken(final String token) {
        System.out.println("Test toke  access...!");
        Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token);
    }
    private Key getSignInKey() {
        byte[] keyBytes= Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
