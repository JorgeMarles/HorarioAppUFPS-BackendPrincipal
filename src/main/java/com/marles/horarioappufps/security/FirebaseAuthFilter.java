package com.marles.horarioappufps.security;

import com.google.firebase.auth.FirebaseAuthException;
import com.marles.horarioappufps.model.User;
import com.marles.horarioappufps.service.FirebaseAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class FirebaseAuthFilter extends OncePerRequestFilter {

    @Autowired
    private FirebaseAuthService firebaseAuthService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                User user = firebaseAuthService.getUserByToken(token);

                UserPrincipal userPrincipal = new UserPrincipal(user);

                Authentication auth = new UsernamePasswordAuthenticationToken(user, null, userPrincipal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (FirebaseAuthException e) {
                log.error("Token validation failed: {}", e.getMessage());
                // Don't set authentication - the request will be unauthorized
            }
        }
        filterChain.doFilter(request, response);
    }
}
