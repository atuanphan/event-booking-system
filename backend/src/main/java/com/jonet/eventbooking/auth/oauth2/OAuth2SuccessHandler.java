package com.jonet.eventbooking.auth.oauth2;

import java.io.IOException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.jonet.eventbooking.auth.JwtService;
import com.jonet.eventbooking.entity.UserEntity;
import com.jonet.eventbooking.enums.AuthProvider;
import com.jonet.eventbooking.model.MyUserDetails;
import com.jonet.eventbooking.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserService userService;

    @Value("${jonet.redirect-url}")
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = extractRegistrationId(authentication); // "google" hoặc "facebook"

        String email, fullname, providerId;

        if ("google".equals(registrationId)) {
            email = oAuth2User.getAttribute("email");
            fullname = oAuth2User.getAttribute("name");
            providerId = oAuth2User.getAttribute("sub");
        } else { // facebook
            email = oAuth2User.getAttribute("email"); // có thể null nếu FB không cấp quyền email
            fullname = oAuth2User.getAttribute("name");
            providerId = oAuth2User.getAttribute("id");
        }

        AuthProvider provider = "google".equals(registrationId) ? AuthProvider.GOOGLE : AuthProvider.FACEBOOK;

        UserEntity user = userService.findOrCreateByEmail(email, fullname, provider, providerId);

        String accessToken = jwtService.generateAccessToken(MyUserDetails.build(user));
        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true) // bắt buộc true nếu SameSite=None
                .sameSite("None") // vì frontend (3000) và backend (8044) khác origin
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        String redirect = redirectUrl + "/oauth-callback";
        response.sendRedirect(redirect);
    }

    private String extractRegistrationId(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            return oauthToken.getAuthorizedClientRegistrationId();
        }
        return null;
    }

}
