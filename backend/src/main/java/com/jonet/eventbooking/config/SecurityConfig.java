package com.jonet.eventbooking.config;

import com.jonet.eventbooking.auth.JwtService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jonet.eventbooking.auth.JwtAuthenticationEntryPoint;
import com.jonet.eventbooking.auth.JwtFilter;
import com.jonet.eventbooking.auth.oauth2.CustomOAuth2UserService;
import com.jonet.eventbooking.auth.oauth2.OAuth2SuccessHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j 
public class SecurityConfig {
	private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2SuccessHandler oauth2SuccessHandler;
	private final PasswordEncoderConfig passwordEncoderConfig;

	@Value("${jonet.redirect-url}")
    private String redirectUrl;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
		    .csrf(csrf -> csrf.disable())
			.cors(Customizer.withDefaults())
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.exceptionHandling(ex -> ex
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint))
		    .authorizeHttpRequests(auth -> auth
				    .requestMatchers("/login").permitAll()
		    	    .requestMatchers("/v1/admin/**").hasRole("ADMIN")
		    	    .requestMatchers("/v1/checkin/**").hasAnyRole("STAFF", "ADMIN")
		    	    .requestMatchers("/v1/events", "/v1/events/**").permitAll()
					.requestMatchers("/v1/auth/login", "/v1/auth/refresh", "/v1/auth/oauth/exchange").permitAll()
					.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
					.requestMatchers("/v1/auth/me").authenticated()
		    	    .anyRequest().authenticated())
			.oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)) //nhận info từ Google trả về, tạo/merge vào database
            .successHandler(oauth2SuccessHandler) // custom: tạo/merge user, phát JWT
            .failureHandler((request, response, exception) -> {
                log.error("OAuth2 login failed", exception);
                String errorMessage = URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
                response.sendRedirect(redirectUrl + "/oauth-callback?error=" + errorMessage);
            }))
			.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
       
		return http.build();
	}
	
	@Bean
	public JwtFilter jwtFilter() {
		return new JwtFilter(jwtService);
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoderConfig.encoder());
		return authenticationProvider;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
