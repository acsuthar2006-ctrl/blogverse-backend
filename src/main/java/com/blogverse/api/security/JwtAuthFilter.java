package com.blogverse.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final CustomUserDetailsService customUserDetailsService;

	/*
	 * WHY GUARD CLAUSES OVER NESTED IF BLOCKS?
	 *
	 * We handle all the "get out early" cases at the TOP of the method.
	 * Each guard clause checks one condition and returns immediately if it fails.
	 * This keeps the happy path (the actual logic) flat and readable at the bottom.
	 *
	 * AVOID THIS — deep nesting, hard to read:
	 *
	 *     if (email != null) {
	 *         if (auth == null) {
	 *             if (tokenValid) {
	 *                 // actual logic buried 3 levels deep
	 *             }
	 *         }
	 *     }
	 *
	 * PREFER THIS — guard clauses, flat and clean:
	 *
	 *     if (email == null) return;
	 *     if (auth != null) return;
	 *     if (!tokenValid) return;
	 *     // actual logic here, no nesting
	 *
	 * Rule: if your code goes more than 2 levels deep,
	 * ask yourself — can I return early instead?
	 */

	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain filterChain) throws ServletException, IOException
	{
//		collecting the token from the request object
		String authorization = request.getHeader("Authorization");
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authorization.substring(7);
		String email = jwtService.getUsernameFromToken(token);

//		checking if the current user is authenticated or not by using the email
		if (email == null || SecurityContextHolder.getContext().getAuthentication() != null) {
			filterChain.doFilter(request, response);
			return;
		}

		UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

//		checking the validity of the token(using the expiration time and the username matching)
		if (!jwtService.isTokenValid(token, userDetails)) {
			filterChain.doFilter(request, response);
			return;
		}

		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(
					userDetails,
				null,
				userDetails.getAuthorities()
		);
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		filterChain.doFilter(request, response);
	}
}
