package com.starterkit.springboot.config;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final String readKey;
    private final String adminKey;

    public ApiKeyAuthFilter(String readKey, String adminKey) {
        this.readKey = readKey;
        this.adminKey = adminKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String auth = request.getHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                String key = auth.substring("Bearer ".length()).trim();
                if (!key.isEmpty() && adminKey != null && adminKey.equals(key)) {
                    setAuth("ADMIN");
                } else if (!key.isEmpty() && readKey != null && readKey.equals(key)) {
                    setAuth("READ");
                }
            }
        }
        chain.doFilter(request, response);
    }

    private void setAuth(String role) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "api-key",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
