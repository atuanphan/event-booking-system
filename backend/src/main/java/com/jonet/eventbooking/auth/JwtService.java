package com.jonet.eventbooking.auth;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.dto.RefreshTokenPayload;
import com.jonet.eventbooking.model.MyUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {
	@Value("${jwt.secret}")
    private String secret;

    @Value("${token.access.expiry}")
    private long accessTokenExpiry;

    @Value("${token.refresh.expiry}")
    private long refreshTokenExpiry;

	private final RedisTemplate<Object, Object> redisTemplate;

    public String generateAccessToken(MyUserDetails user) {
        return Jwts.builder()
            .setSubject(user.getId().toString())
            .claim("email", user.getEmail())
            .claim("roles", user.getRoles())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiry))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public ResponseCookie generateRefreshToken(MyUserDetails user) {
        String token = Jwts.builder()
            .setSubject(user.getId().toString())
            .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiry))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
        RefreshTokenPayload payload = new RefreshTokenPayload(user.getId(), user.getEmail(), user.getRoles());
		redisTemplate.opsForValue().set("refresh-token:" + token, payload, Duration.ofMillis(refreshTokenExpiry));
        return ResponseCookie.from("refresh_token", token)
        		.httpOnly(true)
        		.secure(false)
        		.sameSite("Lax")
        		.path("/")
        		.maxAge(refreshTokenExpiry)
        		.build();
    }

    public Claims validateAndParse(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
