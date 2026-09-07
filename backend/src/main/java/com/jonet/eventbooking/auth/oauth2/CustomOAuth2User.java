package com.jonet.eventbooking.auth.oauth2;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.jonet.eventbooking.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomOAuth2User implements OAuth2User, OidcUser{
    private final OAuth2User oauth2User;
    private UserEntity userEntity;
    private Map<String, Object> attributes;
    private final OidcUser oidcUser; 

    public CustomOAuth2User(UserEntity user, Map<String, Object> attributes) {
        this.attributes = attributes;
        this.userEntity = user;
        this.oidcUser = null;
        this.oauth2User = null;
    }

    // Constructor dùng cho OIDC (Google...)
    public CustomOAuth2User(OAuth2User oauth2User, OidcUser oidcUser, UserEntity user) {
        this.oauth2User = oauth2User;
        this.oidcUser = oidcUser;
        this.userEntity = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userEntity.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getCode())).collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return userEntity.getEmail();
    }

    @Override
    public Map<String, Object> getClaims() {
       return oidcUser != null ? oidcUser.getClaims() : null;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUser != null ? oidcUser.getUserInfo() : null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcUser != null ? oidcUser.getIdToken() : null;
    }
}
