package co.com.pragma.usecase.user;

import co.com.pragma.model.auth.gateways.PasswordEncoderGateway;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserGateway;
import co.com.pragma.model.exception.ResourceConflictException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {
    private final UserGateway userGateway;
    private final PasswordEncoderGateway passwordEncoder;

    public Mono<User> create(User user) {
        return userGateway.existUserByEmailAndDocument(user.getEmail(), user.getDocumentNumber())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) return Mono.error(new ResourceConflictException("User already exists"));

                    return passwordEncoder.encode(user.getPassword())
                            .flatMap(encodedPassword -> {
                                User userWithEncodedPassword = user.toBuilder()
                                        .password(encodedPassword)
                                        .build();
                                return userGateway.saveUser(userWithEncodedPassword);
                            });
                });
    }

    public Flux<User> getAllUsers() {
        return userGateway.findAll();
    }
    
    public Mono<Boolean> validateExistsByEmailAndDocument(String email, String documentNumber) {
        return userGateway.existUserByEmailAndDocument(email, documentNumber);
    }

    public Mono<User> existUserByEmail(String email) {
        return userGateway.existUserByEmail(email);
    }
}