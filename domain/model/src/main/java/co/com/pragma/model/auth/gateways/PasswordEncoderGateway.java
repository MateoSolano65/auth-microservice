package co.com.pragma.model.auth.gateways;

import reactor.core.publisher.Mono;

public interface PasswordEncoderGateway {
    Mono<String> encode(String password);
    Mono<Boolean> matches (String password, String hash);
}
