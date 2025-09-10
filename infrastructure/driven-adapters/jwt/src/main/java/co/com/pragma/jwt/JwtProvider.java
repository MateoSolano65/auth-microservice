package co.com.pragma.jwt;

import co.com.pragma.model.auth.Auth;
import co.com.pragma.model.auth.gateways.AuthProviderGateway;
import co.com.pragma.model.exception.AuthenticationException;
import co.com.pragma.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.lang.Objects;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider implements AuthProviderGateway {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Integer expiration;

    private SecretKey getKey(String secret) {
        byte[] secretBytes = Decoders.BASE64URL.decode(secret);
        return Keys.hmacShaKeyFor(secretBytes);
    }

    @Override
    public Mono<Auth> authenticate(User user) {
        String token = Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole())
                .claim("documentNumber", user.getDocumentNumber())
                .claim("email", user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + (expiration * 1000L)))
                .signWith(getKey(secret))
                .compact();
        return Mono.just(new Auth(token));
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Mono<Boolean> validate(String token) {
        return Mono.fromCallable(() -> {
            Jwts.parser()
                    .verifyWith(getKey(secret))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return true;
        }).onErrorResume(e -> {
            log.error("Token validation failed: {}", e.getMessage());
            return Mono.just(false);
        });
    }

    @Override
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromSupplier(() -> {
                    String subject = Jwts.parser()
                            .verifyWith(getKey(secret))
                            .build()
                            .parseSignedClaims(token)
                            .getPayload()
                            .getSubject();
                    return !Objects.isEmpty(subject);
                })
                .onErrorMap(e -> new AuthenticationException("Token inválido"));
    }

    @Override
    public Mono<String> getSubject(String token) {
        return Mono.fromCallable(() -> Jwts.parser()
                .verifyWith(getKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject())
                .onErrorMap(e -> new AuthenticationException("Token inválido"));
    }
}
