package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserGateway;
import co.com.pragma.model.exception.ResourceConflictException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {
    private final UserGateway userGateway;

    public Mono<User> create(User user) {

        return userGateway.existUserByEmailAndDocument(user.getEmail(), user.getDocumentNumber())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) return Mono.error(new ResourceConflictException("User already exists"));
                    return userGateway.saveUser(user);
                });
    }

    public Flux<User> getAllUsers() {
        return userGateway.findAll();
    }
    
    public Mono<Boolean> validateExistsByEmailAndDocument(String email, String documentNumber) {
        return userGateway.existUserByEmailAndDocument(email, documentNumber);
    }
}