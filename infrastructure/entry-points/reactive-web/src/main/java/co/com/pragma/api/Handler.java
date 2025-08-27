package co.com.pragma.api;

import co.com.pragma.api.dto.ErrorResponse;
import co.com.pragma.api.dto.UserDto;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final UserUseCase userUseCase;
    private final UserMapper userMapper;

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserDto.class)
                .map(userMapper::toEntity)
                .flatMap(userUseCase::createUser)
                .map(userMapper::toDto)
                .flatMap(userDto -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userDto))
                .onErrorResume(this::handleError);
    }

    public Mono<ServerResponse> getUserById(ServerRequest serverRequest) {
        String idParam = serverRequest.pathVariable("id");
        Long userId;
        
        try {
            userId = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            return createErrorResponse("Invalid ID format", "ID must be a number", HttpStatus.BAD_REQUEST);
        }
        
        return userUseCase.getUserById(userId)
                .map(userMapper::toDto)
                .flatMap(userDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userDto))
                .switchIfEmpty(createErrorResponse("User not found", "No user found with id: " + userId, HttpStatus.NOT_FOUND))
                .onErrorResume(this::handleError);
    }

    public Mono<ServerResponse> getUserByEmail(ServerRequest serverRequest) {
        String email = serverRequest.pathVariable("email");
        
        return userUseCase.getUserByEmail(email)
                .map(userMapper::toDto)
                .flatMap(userDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userDto))
                .switchIfEmpty(createErrorResponse("User not found", "No user found with email: " + email, HttpStatus.NOT_FOUND))
                .onErrorResume(this::handleError);
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
      return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(userUseCase.getAllUsers().map(userMapper::toDto), UserDto.class);
    }
    
    private Mono<ServerResponse> handleError(Throwable error) {
        return createErrorResponse("Internal server error", error.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private Mono<ServerResponse> createErrorResponse(String error, String message, HttpStatus status) {
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ErrorResponse.builder()
                        .error(error)
                        .message(message)
                        .status(status.value())
                        .build());
    }
}
