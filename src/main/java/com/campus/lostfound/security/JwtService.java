package com.campus.lostfound.security;

import com.campus.lostfound.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final AppProperties appProperties;

    public String createUserToken(long userId) {
        return buildToken(userId, "user", appProperties.getJwt().getUserSecret(),
                appProperties.getJwt().getUserExpireDays() * 24L * 3600);
    }

    public String createAdminToken(long userId) {
        return buildToken(userId, "admin", appProperties.getJwt().getAdminSecret(),
                appProperties.getJwt().getAdminExpireHours() * 3600L);
    }

    private String buildToken(long userId, String typ, String secret, long seconds) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("typ", typ)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(seconds)))
                .signWith(key)
                .compact();
    }

    public Claims parseUserToken(String token) {
        return parse(token, appProperties.getJwt().getUserSecret());
    }

    public Claims parseAdminToken(String token) {
        return parse(token, appProperties.getJwt().getAdminSecret());
    }

    private Claims parse(String token, String secret) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
