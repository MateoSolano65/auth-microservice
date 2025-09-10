package co.com.pragma.model.auth.gateways;

import co.com.pragma.model.auth.Auth;
import co.com.pragma.model.user.User;
import reactor.core.publisher.Mono;

public interface AuthProviderGateway {
    Mono<Auth> authenticate(User user);
    
    Mono<Boolean> validateToken(String token);
    
    Mono<String> getSubject(String token);
}
