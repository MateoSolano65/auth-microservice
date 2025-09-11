package co.com.pragma.usecase.auth;

import co.com.pragma.model.auth.Auth;
import co.com.pragma.model.auth.gateways.AuthProviderGateway;
import co.com.pragma.model.auth.gateways.PasswordEncoderGateway;
import co.com.pragma.model.exception.AuthenticationException;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthUseCase {
    private final UserUseCase userUseCase;
    private final AuthProviderGateway authProviderGateway;
    private final PasswordEncoderGateway passwordEncoder;

    public Mono<Auth> signIn(String email, String password) {
        return userUseCase.existUserByEmail(email)
                .switchIfEmpty(Mono.error(new AuthenticationException("invalid credentials")))
                .flatMap(user ->
                        passwordEncoder.matches(password, user.getPassword())
                                .flatMap(match -> {
                                    if (!match.equals(Boolean.TRUE)){
                                        return Mono.error(new AuthenticationException("invalid credentials"));
                                    }
                                    return authProviderGateway.authenticate(user);
                                })
                );
    }

    public Mono<User> validateToken(String token) {
        return authProviderGateway.validateToken(token)
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        return authProviderGateway.getSubject(token)
                                .flatMap(userUseCase::existUserByEmail);
                    }
                    return Mono.empty();
                });
    }
}
