package com.jonet.eventbooking.auth;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.auth.oauth2.CustomOAuth2User;
import com.jonet.eventbooking.entity.RoleEntity;
import com.jonet.eventbooking.entity.UserEntity;
import com.jonet.eventbooking.enums.AuthProvider;
import com.jonet.eventbooking.repository.RoleRepository;
import com.jonet.eventbooking.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class CustomOidcUserService extends OidcUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // vd: "google"

        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();
        String providerId = oidcUser.getSubject(); // "sub" claim

        if (email == null) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error("email_not_found"),
                "Email not found from OIDC provider"
            );
        }

        // Tìm user theo email, nếu chưa có thì tạo mới, nếu có thì cập nhật
        UserEntity user = userRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, name))
                .orElseGet(() -> registerNewUser(registrationId, providerId, email, name));

        // Trả về CustomOAuth2User bọc oidcUser, đồng thời truyền thêm thông tin user trong DB nếu cần
        return new CustomOAuth2User(oidcUser, oidcUser, user);
    }

    private UserEntity registerNewUser(String provider, String providerId, String email, String name) {
        RoleEntity role = roleRepository.findByCode("CUSTOMER")
                .orElseThrow(() -> new IllegalStateException("Default role ROLE_USER not found in DB"));
        UserEntity newUser = UserEntity.builder()
                .provider(AuthProvider.valueOf(provider.toUpperCase()))
                .providerId(providerId)
                .email(email)
                .fullname(name)
                .roles(new ArrayList<>(List.of(role))) // enum Role tùy hệ thống của bạn
                .build();
        return userRepository.save(newUser);
    }

    private UserEntity updateExistingUser(UserEntity existingUser, String name) {
        existingUser.setFullname(name);
        return userRepository.save(existingUser);
    }
}
