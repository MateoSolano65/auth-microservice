package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserGateway {
  Mono<User> saveUser(User user);
  Mono<Boolean> existUserByEmailAndDocument(String email, String documentNumber);
  Flux<User> findAll();
  Mono<User> existUserByEmail(String email);
}
