package com.launchgate.identity.security;

import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.launchgate.common.ApiError;
import com.launchgate.common.DomainException;
import com.launchgate.identity.service.AuthenticationService;
import com.launchgate.identity.dto.AuthenticatedUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationService authenticationService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || authorization.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            var user = authenticationService.requireAuthenticatedUser(authorization);
            SecurityContextHolder.getContext().setAuthentication(authentication(user));
            filterChain.doFilter(request, response);
        } catch (DomainException exception) {
            SecurityContextHolder.clearContext();
            writeUnauthorized(response, exception);
        }
    }

    private UsernamePasswordAuthenticationToken authentication(AuthenticatedUser user) {
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.accountType().name()));
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }

    private void writeUnauthorized(HttpServletResponse response, DomainException exception) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        var error = new ApiError(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                exception.code(),
                exception.getMessage(),
                Map.of()
        );
        objectMapper.writeValue(response.getOutputStream(), error);
    }
}
