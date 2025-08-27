package co.com.pragma.api;

import co.com.pragma.api.dto.UserDto;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.api.validator.ValidatorDTO;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final UserUseCase userUseCase;
    private final UserMapper userMapper;
    private final ValidatorDTO validatorDTO;

    public Mono<ServerResponse> createUserPost(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserDto.class)
                .flatMap(validatorDTO::validate)
                .map(userMapper::toEntity)
                .flatMap(userUseCase::createUser)
                .map(userMapper::toDto)
                .flatMap(userDto -> ServerResponse.status(HttpStatus.CREATED).bodyValue(userDto));
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
      return userUseCase.getAllUsers()
        .map(userMapper::toDto)
        .collectList()
        .flatMap(userDtos -> ServerResponse.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(userDtos));
    }
}
