package com.sitare.authservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final Key secretKey;
  private static final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 60 * 10; // 10 hours
  private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 30; // 30 days

  public JwtUtil(@Value("${jwt.secret}") String secret) {
    byte[] keyBytes;
    try {
      // Try to decode as base64 first
      keyBytes = Base64.getDecoder().decode(secret);
    } catch (IllegalArgumentException e) {
      // If not base64, use the string bytes directly
      // Ensure minimum 32 bytes (256 bits) for HMAC-SHA256
      byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
      if (secretBytes.length < 32) {
        // Pad or repeat to ensure minimum length
        byte[] padded = new byte[32];
        System.arraycopy(secretBytes, 0, padded, 0, Math.min(secretBytes.length, 32));
        for (int i = secretBytes.length; i < 32; i++) {
          padded[i] = secretBytes[i % secretBytes.length];
        }
        keyBytes = padded;
      } else {
        keyBytes = secretBytes;
      }
    }
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(String email, String role, Map<String, Object> extraClaims) {
    JwtBuilder builder = Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
        .signWith(secretKey);
    
    builder.claim("role", role);
    if (extraClaims != null) {
      for (Map.Entry<String, Object> entry : extraClaims.entrySet()) {
        builder.claim(entry.getKey(), entry.getValue());
      }
    }
    
    return builder.compact();
  }

  public String generateRefreshToken(String email, String role, Map<String, Object> extraClaims) {
    JwtBuilder builder = Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
        .signWith(secretKey);
    
    builder.claim("role", role);
    builder.claim("type", "refresh");
    if (extraClaims != null) {
      for (Map.Entry<String, Object> entry : extraClaims.entrySet()) {
        builder.claim(entry.getKey(), entry.getValue());
      }
    }
    
    return builder.compact();
  }

  public void validateToken(String token) {
    try {
      JwtParser parser = Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build();
      parser.parseClaimsJws(token);
    } catch (JwtException e) {
      throw new JwtException("Invalid JWT");
    }
  }

  public Claims extractClaims(String token) {
    try {
      JwtParser parser = Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build();
      return parser.parseClaimsJws(token).getBody();
    } catch (JwtException e) {
      throw new JwtException("Invalid JWT");
    }
  }

  public String extractEmail(String token) {
    return extractClaims(token).getSubject();
  }

  public String extractRole(String token) {
    return extractClaims(token).get("role", String.class);
  }

  public String extractUserId(String token) {
    Object userId = extractClaims(token).get("userId");
    return userId != null ? userId.toString() : null;
  }

  public boolean isTokenExpired(String token) {
    try {
      Claims claims = extractClaims(token);
      return claims.getExpiration().before(new Date());
    } catch (JwtException e) {
      return true;
    }
  }

  public long getAccessTokenExpiration() {
    return ACCESS_TOKEN_EXPIRATION / 1000; // Return in seconds
  }
}
