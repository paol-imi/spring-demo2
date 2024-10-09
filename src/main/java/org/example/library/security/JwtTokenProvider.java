package org.example.library.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

/**
 * JwtTokenProvider class for generating and validating JWT tokens.
 */
@Component
public class JwtTokenProvider {
    /**
     * The secret key to use for the JWT token.
     */
    private final SecretKey key;

    /**
     * The expiration time of the JWT token.
     */
    private final long jwtExpirationInMs;

    /**
     * Constructor for the JwtTokenProvider.
     *
     * @param jwtSecret         The secret key to use for the JWT token
     * @param jwtExpirationInMs The expiration time of the JWT token
     */
    public JwtTokenProvider(@Value("${app.jwt.secret}") String jwtSecret,
                            @Value("${app.jwt.expiration}") long jwtExpirationInMs) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    /**
     * Generates a JWT token based on the authentication.
     *
     * @param userPrincipal The user principal to generate the token for
     * @return The generated JWT token
     */
    public String generateToken(UserDetailsImpl userPrincipal) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + this.jwtExpirationInMs);

        return Jwts.builder().subject(userPrincipal.getUsername()).issuedAt(now).expiration(expiryDate)
                .signWith(this.key)
                .compact();
    }

    /**
     * Gets the username from the JWT token.
     *
     * @param token The JWT token to get the username from
     * @return The username from the JWT token
     */
    public Optional<String> getUsernameFromJWT(String token) {
        // TODO: Define a JwtToken class to handle the token parsing
        try {
            Claims claims = Jwts.parser().verifyWith(this.key).build().parseSignedClaims(token).getPayload();
            return Optional.of(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}