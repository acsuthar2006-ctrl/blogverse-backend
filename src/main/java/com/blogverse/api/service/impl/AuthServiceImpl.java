package com.blogverse.api.service.impl;

import com.blogverse.api.domain.entity.Author;
import com.blogverse.api.dto.request.LoginRequest;
import com.blogverse.api.dto.request.RegisterRequest;
import com.blogverse.api.dto.response.AuthResponse;
import com.blogverse.api.exception.EmailAlreadyExistsException;
import com.blogverse.api.repository.AuthorRepository;
import com.blogverse.api.security.JwtService;
import com.blogverse.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AuthorRepository authorRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	@Override
	public AuthResponse register(RegisterRequest registerRequest) {
		String email = registerRequest.email();
		if (authorRepository.existsByEmail(email)) {
			throw new EmailAlreadyExistsException("Email Already Exists");
		}

		String hashesPassword = passwordEncoder.encode(registerRequest.password());

		Author author = Author.builder()
				.email(email)
				.password(hashesPassword)
				.userName(registerRequest.username())
				.fullName(registerRequest.fullName())
				.isActive(true)
				.build();

		authorRepository.save(author);

		String token = jwtService.generateToken(author);
		return new AuthResponse(token, email, author.getRole());
	}

	@Override
	public AuthResponse login(LoginRequest loginRequest) {

		String email = loginRequest.email();
		String password = loginRequest.password();

		var authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(email, password));

		Author author = (Author) authentication.getPrincipal();
		String token = jwtService.generateToken(author);

		return new AuthResponse(token, email, author.getRole());
	}
}
