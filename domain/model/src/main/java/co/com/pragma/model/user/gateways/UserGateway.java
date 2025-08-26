package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Mono;

public interface UserGateway {
  Mono<User> saveUser(User user);
  Mono<User> findById(Long id);
  Mono<User> findByEmail(String email);
}
