package co.com.pragma.usecase.auth;

import co.com.pragma.model.auth.Auth;
import co.com.pragma.model.auth.gateways.AuthProviderGateway;
import co.com.pragma.model.exception.BusinessRuleViolationException;
import co.com.pragma.model.exception.ResourceConflictException;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthUseCase {
    private final UserUseCase userUseCase;
    private final AuthProviderGateway authProviderGateway;

//    public Mono<Auth> signin(String email, String password){
//        userUseCase.existUserByEmail(email)
//                .switchIfEmpty(Mono.error(new ResourceConflictException("User already exists")))
//                .flatMap(user -> {
//
//                });
//    }
}
