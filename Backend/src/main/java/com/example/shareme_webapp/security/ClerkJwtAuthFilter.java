package com.example.shareme_webapp.security;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(ClerkJwtAuthFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().contains("/webhooks") ||
             request.getRequestURI().contains("/public") ||
            request.getRequestURI().contains("/download")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        log.info(
                "Clerk auth request: method={}, uri={}, origin={}, authHeaderPresent={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getHeader("Origin"),
                authHeader != null
        );

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            log.warn(
                    "Authorization header missing or invalid for {}",
                    request.getRequestURI()
            );

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Authorization header missing/invalid"
            );

            return;
        }

        try {
            String token= authHeader.substring(7);
            String[] chunks=token.split("\\.");
            if(chunks.length<3){
                response.sendError(HttpServletResponse.SC_FORBIDDEN,"Invalid JWT token format");
                return;
            }

            String headerJson = new String(Base64.getUrlDecoder().decode(chunks [0]));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode headerNode = mapper.readTree(headerJson);

            if (!headerNode.has("kid")) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Token header is missing kid"
                );
                return;
            }

            String kid = headerNode.get("kid").asText();

            String algorithm = headerNode
                    .path("alg")
                    .asText();

            if (!"RS256".equals(algorithm)) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Unsupported JWT algorithm"
                );
                return;
            }

            log.info("JWT kid received: {}", kid);
            log.info("Expected Clerk issuer: {}", clerkIssuer);

            PublicKey publicKey = jwksProvider.getPublicKey(kid);

            Claims claims = Jwts.parser()
                    .setSigningKey(publicKey)
                    .setAllowedClockSkewSeconds(60)
                    .requireIssuer(clerkIssuer)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String clerkId = claims.getSubject();

            if (clerkId == null || clerkId.isBlank()) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "JWT subject missing"
                );
                return;
            }

            log.info(
                    "JWT authentication successful. Clerk ID: {}",
                    clerkId
            );
            log.info(
                    "JWT verified: subject={}, issuer={}, expiration={}",
                    claims.getSubject(),
                    claims.getIssuer(),
                    claims.getExpiration()
            );
            UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(clerkId,null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request,response);

        } catch (Exception e) {

            log.error(
                    "Clerk JWT authentication failed for {}: {}",
                    request.getRequestURI(),
                    e.getMessage(),
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid JWT token"
            );

            return;
        }

    }
}
