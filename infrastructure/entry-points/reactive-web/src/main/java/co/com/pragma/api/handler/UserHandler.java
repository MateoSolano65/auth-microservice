package co.com.pragma.api.handler;

import co.com.pragma.api.dto.ResponseApiDto;
import co.com.pragma.api.dto.UserDto;
import co.com.pragma.api.dto.UserResponseDto;
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
      summary = "Create a new user",
      requestBody = @io.swagger.v3.oas.annotations.parameters.
          RequestBody(required = true,
              content = @Content(schema = @Schema(implementation = UserDto.class))
          ),
      responses = {
          @ApiResponse(responseCode = "201", description = "User created",
              content = @Content(schema = @Schema(implementation = ResponseApiDto.class))),
          @ApiResponse(responseCode = "400", description = "Invalid data",
              content = @Content(schema = @Schema(implementation = ResponseApiDto.class))),
          @ApiResponse(responseCode = "422", description = "Validation error",
              content = @Content(schema = @Schema(implementation = ResponseApiDto.class))),
          @ApiResponse(responseCode = "500", description = "Internal error",
              content = @Content(schema = @Schema(implementation = ResponseApiDto.class)))
      }
  )
    public Mono<ServerResponse> createUserPost(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserDto.class)
                .flatMap(validatorDTO::validate)
                .map(userMapper::toUserDomain)
                .flatMap(userUseCase::create)
                .map(userMapper::toUserResponseDto)
                .flatMap(userResponse -> {
                    ResponseCode successCode = ResponseCode.USER_CREATED;
                    ResponseApiDto<UserResponseDto> response = ResponseApiDto.<UserResponseDto>builder()
                            .code(successCode.getCodeValue())
                            .message(successCode.getDefaultMessage())
                            .data(userResponse)
                            .build();
                    
                    return ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                });
    }

    @Operation(
        operationId = "getAllUsersSwagger",
        summary = "List users",
        responses = @ApiResponse(responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = ResponseApiDto.class))
        )
    )
    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
      return userUseCase.getAllUsers()
        .map(userMapper::toUserResponseDto)
        .collectList()
        .flatMap(userResponses -> {
            ResponseCode successCode = ResponseCode.USER_FOUND;
            ResponseApiDto<List<UserResponseDto>> response = ResponseApiDto.<List<UserResponseDto>>builder()
                    .code(successCode.getCodeValue())
                    .message(successCode.getDefaultMessage())
                    .data(userResponses)
                    .build();
            
            return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
        })
        .onErrorResume(e -> {
            ResponseApiDto<String> errorResponse = ResponseApiDto.<String>builder()
                    .code(ResponseCode.INTERNAL_SERVER_ERROR.getCodeValue())
                    .message("Error getting users")
                    .error(List.of(e.getMessage()))
                    .build();
            
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(errorResponse);
        });
    }
    
    @Operation(
        operationId = "validateUserExistsSwagger",
        summary = "Validate if a user exists by document and email",
        description = "This endpoint validates if a user exists with the provided email and document. " +
                      "Returns true if it exists or false if it does not exist.",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(
                name = "email",
                description = "Email of the user to validate",
                required = true,
                example = "user@example.com",
                in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY
            ),
            @io.swagger.v3.oas.annotations.Parameter(
                name = "document",
                description = "Document number of the user to validate",
                required = true,
                example = "123456789",
                in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Returns true if it exists or false if it does not exist",
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
