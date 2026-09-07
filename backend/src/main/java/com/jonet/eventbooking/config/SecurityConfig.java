package com.jonet.eventbooking.config;

import com.jonet.eventbooking.auth.JwtService;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2SuccessHandler oauth2SuccessHandler;
	private final PasswordEncoderConfig passwordEncoderConfig;

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
		    	    .requestMatchers("/admin/**").hasRole("ADMIN")
		    	    .requestMatchers("/checkin/**").hasAnyRole("STAFF", "ADMIN")
		    	    .requestMatchers("/events", "/events/**").permitAll()
					.requestMatchers("/auth/login", "/auth/refresh").permitAll()
					.requestMatchers("/auth/me").authenticated()
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
