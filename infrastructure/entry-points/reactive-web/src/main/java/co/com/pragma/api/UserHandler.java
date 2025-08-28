package co.com.pragma.api;

import co.com.pragma.api.dto.ResponseApiDto;
import co.com.pragma.api.dto.UserDto;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.api.validator.ValidatorDTO;
import co.com.pragma.usecase.user.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserHandler {
    private final UserUseCase userUseCase;
    private final UserMapper userMapper;
    private final ValidatorDTO validatorDTO;

    @Operation(
      operationId = "createUserSwagger",
      summary = "Crear un nuevo usuario",
      requestBody = @io.swagger.v3.oas.annotations.parameters.
          RequestBody(required = true,
              content = @Content(schema = @Schema(implementation = UserDto.class))
          ),
      responses = {
          @ApiResponse(responseCode = "201", description = "Usuario creado",
              content = @Content(schema = @Schema(implementation = ResponseApiDto.class))),
          @ApiResponse(responseCode = "400", description = "Datos inválidos",
              content = @Content(schema = @Schema(implementation = ResponseApiDto.class))),
          @ApiResponse(responseCode = "422", description = "Error de validación",
              content = @Content(schema = @Schema(implementation = ResponseApiDto.class))),
          @ApiResponse(responseCode = "500", description = "Error interno",
              content = @Content(schema = @Schema(implementation = ResponseApiDto.class)))
      }
  )
    public Mono<ServerResponse> createUserPost(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserDto.class)
                .flatMap(validatorDTO::validate)
                .map(userMapper::toUserDomain)
                .flatMap(userUseCase::createUser)
                .map(userMapper::toUserDto)
                .flatMap(userDto -> {
                    ResponseApiDto<UserDto> response = ResponseApiDto.<UserDto>builder()
                            .status(HttpStatus.CREATED.value())
                            .message(HttpStatus.CREATED.getReasonPhrase())
                            .data(userDto)
                            .build();
                    
                    return ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                });
    }

    @Operation(
        operationId = "getAllUsersSwagger",
        summary = "Listar usuarios",
        responses = @ApiResponse(responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = ResponseApiDto.class))
        )
    )
    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
      return userUseCase.getAllUsers()
        .map(userMapper::toUserDto)
        .collectList()
        .flatMap(userDtos -> {
            ResponseApiDto<List<UserDto>> response = ResponseApiDto.<List<UserDto>>builder()
                    .status(HttpStatus.OK.value())
                    .message(HttpStatus.OK.getReasonPhrase())
                    .data(userDtos)
                    .build();
                    
            return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
        });
    }
}
