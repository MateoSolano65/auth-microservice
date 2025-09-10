package co.com.pragma.jwt;

import co.com.pragma.model.response.ResponseCode;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationManager(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .map(auth -> auth.getCredentials().toString())
                .flatMap(token -> Mono.fromCallable(() -> jwtProvider.getClaims(token))
                        .onErrorMap(e -> new BadCredentialsException(ResponseCode.UNAUTHORIZED.getDefaultMessage(), e))
                )
                .map(claims -> {
                    String role = (String) claims.get("role");
                    return new UsernamePasswordAuthenticationToken(
                            claims.getSubject(),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                });
    }
}

