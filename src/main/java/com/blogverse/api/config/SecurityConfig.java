package com.blogverse.api.config;

import com.blogverse.api.security.CustomUserDetailsService;
import com.blogverse.api.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomUserDetailsService customUserDetailsService;
	private final JwtAuthFilter jwtAuthFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						// Auth endpoints
						.requestMatchers("/auth/**").permitAll()

						// Posts - public read
						.requestMatchers(HttpMethod.GET, "/posts/**").permitAll()

						// Comments - public CRUD (editToken validated in service layer, admin check for logged-in users)
						.requestMatchers(HttpMethod.GET, "/posts/**", "/comments/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/posts/*/comments").permitAll()
						.requestMatchers(HttpMethod.PUT, "/comments/**").permitAll()
						.requestMatchers(HttpMethod.DELETE, "/comments/**").permitAll()
						// Comment replies - AUTHENTICATED ONLY
						.requestMatchers(HttpMethod.POST, "/comments/*/replies").authenticated()

						// Categories & Tags - public read
						.requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/tags/**").permitAll()
						// Actuator endpoints - public read (or restrict to ADMIN if preferred)
						.requestMatchers("/actuator/**").authenticated()

						// Everything else requires auth
						.anyRequest().authenticated())
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration config) {
		return config.getAuthenticationManager();
	}
}
