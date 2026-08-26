package com.jonet.eventbooking.config;

import com.jonet.eventbooking.auth.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jonet.eventbooking.auth.JwtAuthenticationEntryPoint;
import com.jonet.eventbooking.auth.JwtFilter;
import com.jonet.eventbooking.auth.OAuth2SuccessHandler;
import com.jonet.eventbooking.auth.custom.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2SuccessHandler oauth2SuccessHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
		    .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.exceptionHandling(ex -> ex
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint))
		    .authorizeHttpRequests(auth -> auth
				    .requestMatchers("/login").permitAll()
		    	    .requestMatchers("/api/admin/**").hasRole("ADMIN")
		    	    .requestMatchers("/api/checkin/**").hasAnyRole("STAFF", "ADMIN")
		    	    .requestMatchers("/api/events", "/api/events/**", "/api/auth/**").permitAll()
		    	    .anyRequest().authenticated())
			.oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)) //nhận info từ Google trả về, tạo/merge vào database
            .successHandler(oauth2SuccessHandler) // custom: tạo/merge user, phát JWT
            .failureUrl("/login?error=true"))
			.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
       
		return http.build();
	}
	
	@Bean
	public JwtFilter jwtFilter() {
		return new JwtFilter(jwtService);
	}

	@Bean
	PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
		authenticationProvider.setPasswordEncoder(encoder());
		return authenticationProvider;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
