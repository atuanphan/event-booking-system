package com.jonet.eventbooking.auth;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.model.MyUserDetails;
import com.jonet.eventbooking.utils.RedisUtil;

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

	private final StringRedisTemplate stringRedisTemplate;;

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

    public ResponseCookie generateRefreshToken(UUID userId) {
        String refreshToken = getRefreshToken();
        stringRedisTemplate.opsForValue().set(RedisUtil.getRefreshTokenKey(sha256Hex(refreshToken)), userId.toString(),
                Duration.ofMillis(refreshTokenExpiry));
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofMillis(refreshTokenExpiry))
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

    private static final String getRefreshToken() {
		byte[] bytes = new byte[32];
		new SecureRandom().nextBytes(bytes);
		String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
		return token;
	}

    public static String sha256Hex(String s) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(d);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
