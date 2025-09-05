package co.com.pragma.api;

import co.com.pragma.api.dto.ResponseApiDto;
import co.com.pragma.api.dto.UserDto;
import co.com.pragma.model.response.ResponseCode;
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
                .flatMap(userUseCase::create)
                .map(userMapper::toUserDto)
                .flatMap(userDto -> {
                    ResponseCode successCode = ResponseCode.USER_CREATED;
                    ResponseApiDto<UserDto> response = ResponseApiDto.<UserDto>builder()
                            .code(successCode.getCodeValue())
                            .message(successCode.getDefaultMessage())
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
            ResponseCode successCode = ResponseCode.USER_FOUND;
            ResponseApiDto<List<UserDto>> response = ResponseApiDto.<List<UserDto>>builder()
                    .code(successCode.getCodeValue())
                    .message(successCode.getDefaultMessage())
                    .data(userDtos)
                    .build();
                    
            return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
        });
    }
    
    @Operation(
        operationId = "validateUserExistsSwagger",
        summary = "Validar si existe un usuario por documento y correo",
        description = "Este endpoint valida si existe un usuario con el correo y documento proporcionados. " +
                      "Devuelve true si existe, false si no existe.",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(
                name = "email",
                description = "Correo electrónico del usuario a validar",
                required = true,
                example = "usuario@ejemplo.com",
                in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY
            ),
            @io.swagger.v3.oas.annotations.Parameter(
                name = "document",
                description = "Número de documento del usuario a validar",
                required = true,
                example = "123456789",
                in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Devuelve true si existe o false si no existe",
                content = @Content(schema = @Schema(implementation = Boolean.class))
            )
        }
    )
    public Mono<ServerResponse> validateUserExists(ServerRequest serverRequest) {
        String email = serverRequest.queryParam("email").orElse("");
        String documentNumber = serverRequest.queryParam("document").orElse("");
        
        return userUseCase.validateExistsByEmailAndDocument(email, documentNumber)
            .flatMap(exists -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(exists));
    }
}
