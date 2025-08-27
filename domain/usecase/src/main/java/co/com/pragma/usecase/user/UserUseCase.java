package co.com.pragma.usecase.user;

import java.util.ArrayList;
import java.util.List;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserGateway;
import co.com.pragma.model.exception.ResourceConflictException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {
  private final UserGateway userGateway;

  // mensajes para correo y documento duplicados
  public String emailAlreadyExists = "Email already exists";
  public String documentNumberAlreadyExists = "Document number already exists";

  public Mono<User> createUser(User user) {
    Mono<Boolean> emailExists = userGateway.existUserByEmail(user.getEmail());
    Mono<Boolean> documentNumberExists = userGateway.existUserByDocumentNumber(user.getDocumentNumber());
    return Mono.zip(emailExists, documentNumberExists)
      .flatMap(tuple -> {
        if (tuple.getT1() || tuple.getT2()) {
          List<String> errors = new ArrayList<>();
          errors.add(emailAlreadyExists);
          errors.add(documentNumberAlreadyExists);
          if (!errors.isEmpty()) return Mono.error(new ResourceConflictException(String.join(", ", errors)));
        }
        return userGateway.saveUser(user);
      });
  }

  public Flux<User> getAllUsers() {
      return userGateway.findAll();
  }
}