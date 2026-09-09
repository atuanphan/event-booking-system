package com.jonet.eventbooking.auth.oauth2;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.jonet.eventbooking.auth.JwtService;
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
        ResponseCookie refreshTokenCookie = jwtService.generateRefreshToken(userDetails);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        String codeAuth = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("oauth-code:" + codeAuth, userDetails.getId(), Duration.ofSeconds(30000)); // 30 giây
        String redirect = redirectUrl + "/oauth-callback?code=" + codeAuth;
        
        response.sendRedirect(redirect);
    }

}
