package com.jonet.eventbooking.auth.oauth2;

import java.util.List;
import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.entity.RoleEntity;
import com.jonet.eventbooking.entity.UserEntity;
import com.jonet.eventbooking.enums.AuthProvider;
import com.jonet.eventbooking.repository.RoleRepository;
import com.jonet.eventbooking.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j 
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Gọi Google lấy thông tin user (tự động theo chuẩn OAuth2)
        OAuth2User oauth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "google", "facebook"...
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String providerId = "google".equals(registrationId)
            ? (String) attributes.get("sub")
            : (String) attributes.get("id");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        // Tìm user theo email, nếu chưa có thì tạo mới
        UserEntity user = userRepository.findByEmailWithRoles(email)
                .map(existing -> updateExistingUser(existing, name))
                .orElseGet(() -> registerNewUser(email, name, registrationId, providerId));

        userRepository.save(user);

        // Trả về OAuth2User để Spring Security dùng làm Authentication principal
        return new CustomOAuth2User(user, attributes);
    }

    private UserEntity registerNewUser(String email, String name, String provider, String providerId) {
        RoleEntity role = roleRepository.findByCode("CUSTOMER")
                .orElseThrow(() -> new IllegalStateException("Default role ROLE_USER not found in DB"));
        return UserEntity.builder()
                    .email(email)
                    .fullname(name)
                    .provider(AuthProvider.valueOf(provider.toUpperCase()))
                    .providerId(providerId)
                    .password(null)
                    .roles(List.of(role))
                    .build();
    }

    private UserEntity updateExistingUser(UserEntity existing, String name) {
        existing.setFullname(name);
        return existing;
    }
}
