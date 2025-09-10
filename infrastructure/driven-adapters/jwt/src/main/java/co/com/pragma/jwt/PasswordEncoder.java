package co.com.pragma.jwt;

import co.com.pragma.model.auth.gateways.PasswordEncoderGateway;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class PasswordEncoder implements PasswordEncoderGateway {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Mono<String> encode(String password) {
        return Mono.just(bCryptPasswordEncoder.encode(password));
    }

    @Override
    public Mono<Boolean> matches(String password, String hash) {
        return Mono.just(bCryptPasswordEncoder.matches(password, hash));
    }
}
