package org.springframework.samples.petclinic.consultation;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JoinTokenService {

    private final SecretKey key;
    private final Duration validity;

    public JoinTokenService(@Value("${video.consultation.jwt.secret}") String secret,
            @Value("${video.consultation.jwt.validity-minutes:5}") long validityMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.validity = Duration.ofMinutes(validityMinutes);
    }

    public String createJoinToken(String appointmentId, Integer userId, String role) {
        Instant now = Instant.now();
        Instant exp = now.plus(validity);

        return Jwts.builder()
                .subject(appointmentId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("uid", userId)
                .claim("role", role)
                .signWith(key)
                .compact();
    }

    public long getExpiresInSeconds() {
        return validity.toSeconds();
    }
}
