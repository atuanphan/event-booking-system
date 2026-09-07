package com.jonet.eventbooking.model;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.jonet.eventbooking.entity.UserEntity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MyUserDetails implements UserDetails{
	private static final long serialVersionUID = 1L;
	
	private UUID id;
	private String password;
	private String fullname;
	private String email;
	private List<String> roles;
	private String provider;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;
	private Collection<? extends GrantedAuthority> authorities;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	public static MyUserDetails build(UserEntity userEntity) {
		List<String> roles = userEntity.getRoles().stream().map(role -> role.getCode()).collect(Collectors.toList());
		List<GrantedAuthority> authorities = roles.stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList());
		return MyUserDetails.builder()
				.id(userEntity.getId())
				.fullname(userEntity.getFullname())
				.email(userEntity.getEmail())
				.password(userEntity.getPassword())
				.roles(roles)
				.provider(userEntity.getProvider().name())
				.accountNonExpired(true)
				.accountNonLocked(true)
				.credentialsNonExpired(true)
				.enabled(true)
				.authorities(authorities)
				.build();
	}
}
