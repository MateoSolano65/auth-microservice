package co.com.pragma.jwt;

import co.com.pragma.model.auth.Auth;
import co.com.pragma.model.auth.gateways.AuthProviderGateway;
import co.com.pragma.model.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;

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
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + (expiration * 1000L)))
                .signWith(getKey(secret))
                .compact();
        return Mono.just(new Auth(token));
    }
}
