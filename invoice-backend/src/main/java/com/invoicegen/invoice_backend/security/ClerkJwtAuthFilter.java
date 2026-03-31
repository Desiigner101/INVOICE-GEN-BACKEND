package com.invoicegen.invoice_backend.security;

import com.invoicegen.invoice_backend.config.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class ClerkJwtAuthFilter extends OncePerRequestFilter {

    @Value("${clerk.issuer}")
    private String clerkIssuer;

    private final ClerkJwksProvider jwksProvider;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().contains("/api/webhooks") ||
                request.getRequestURI().contains("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Authorization header missing/invalid");
            return;
        }

        String token = authHeader.substring(7);

        // Try mobile JWT first
        if (jwtUtil.validateToken(token)) {
            String userId = jwtUtil.extractUserId(token);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
            return;
        }

        // Fall back to Clerk JWT
        try {
            String[] chunks = token.split("\\.");
            String headerJson = new String(Base64.getUrlDecoder().decode(chunks[0]));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode headerNode = mapper.readTree(headerJson);
            String kid = headerNode.get("kid").asText();

            PublicKey publicKey = jwksProvider.getPublicKey(kid);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .setAllowedClockSkewSeconds(60)
                    .requireIssuer(clerkIssuer)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String clerkUserId = claims.getSubject();

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    clerkUserId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid JWT token");
        }
    }
}