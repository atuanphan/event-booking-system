package com.jonet.eventbooking.auth.oauth2;

import java.io.IOException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.jonet.eventbooking.auth.JwtService;
import com.jonet.eventbooking.dto.RefreshTokenPayload;
import com.jonet.eventbooking.entity.UserEntity;
import com.jonet.eventbooking.model.MyUserDetails;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;

    @Value("${jonet.redirect-url}")
    private String redirectUrl;

    @Value("${token.refresh.expiry}")
    private Long refreshTokenExpiration;

    private final RedisTemplate<Object, Object> redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        UserEntity user = oAuth2User.getUserEntity();

        MyUserDetails userDetails = MyUserDetails.build(user);
        String accessToken = jwtService.generateAccessToken(userDetails);
        ResponseCookie refreshTokenCookie = jwtService.generateRefreshToken(userDetails);
        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false) // bắt buộc true nếu SameSite=None
                .sameSite("Lax") // vì frontend (3000) và backend (8044) khác origin
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        redisTemplate.opsForValue().set("refresh-token:" + refreshTokenCookie.getValue(),
                new RefreshTokenPayload(userDetails.getId(), userDetails.getEmail(), userDetails.getRoles()),
                Duration.ofMillis(refreshTokenExpiration));
                
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        String redirect = redirectUrl + "/oauth-callback";
        response.sendRedirect(redirect);
    }

}
