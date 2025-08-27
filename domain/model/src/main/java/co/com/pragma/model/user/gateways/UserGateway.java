package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserGateway {
  Mono<User> save(User user);
  Mono<Boolean> existUserByDocumentNumber(String documentNumber);
  Mono<Boolean> existUserByEmail(String email);
  Flux<User> findAll();
}
