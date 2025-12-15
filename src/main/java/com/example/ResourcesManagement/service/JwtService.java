package com.example.ResourcesManagement.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders; // <-- Thêm import
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // Tự định nghĩa một chuỗi bí mật. Chuỗi này phải đủ dài và phức tạp.
    // Tối thiểu 256 bit, tương đương 32 ký tự an toàn (encoded Base64)
    private static final String SECRET_KEY = "VGhlUXVpY2tCcm93bkZveEp1bXBzT3ZlckxvY2FsSG9zdA==";



    // Phương thức helper để tạo key từ chuỗi bí mật
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Lấy thông tin Claims từ token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // <-- Sửa ở đây
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    // Tạo token từ username
    public String generateToken(String username) {
        long jwtExpiration = 3600000;
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // <-- Sửa ở đây
                .compact();
    }

    // Các phương thức còn lại giữ nguyên
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public boolean isTokenValid(String token, String username) {
        return (extractUsername(token).equals(username)) && !isTokenExpired(token);
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}