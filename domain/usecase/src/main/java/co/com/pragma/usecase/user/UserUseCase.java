package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {
  private final UserGateway userGateway;

  public Mono<User> createUser(User user) {
    return userGateway.save(user);
  }

  public Mono<User> getUserById(Long id) {
    return userGateway.findById(id);
  }

  public Mono<User> getUserByEmail(String email) {
    return userGateway.findByEmail(email);
  }

  public Flux<User> getAllUsers() {
      return userGateway.findAll();
  }
}