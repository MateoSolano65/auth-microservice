package co.com.pragma.api.handler;

import co.com.pragma.api.dto.ResponseApiDto;
import co.com.pragma.api.dto.request.SignInDTO;
import co.com.pragma.api.dto.request.ValidateTokenDTO;
import co.com.pragma.api.dto.response.ValidationTokenResponseDTO;
import co.com.pragma.api.mapper.ValidationTokenMapper;
import co.com.pragma.model.auth.Auth;
import co.com.pragma.model.response.ResponseCode;
import co.com.pragma.usecase.auth.AuthUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthHandler {
    private final AuthUseCase authUseCase;
    private final ValidationTokenMapper validationTokenMapper;


    @Operation(
            operationId = "login",
            summary = "User login",
            description = "Authenticates a user and returns an authentication token",
            requestBody = @RequestBody(
                    required = true,
                    description = "The user's credentials (email and password)",
                    content = @Content(schema = @Schema(implementation = SignInDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successful"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Invalid credentials"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Missing or invalid fields"
                    )
            }
    )


    public Mono<ServerResponse> listenSignIn(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(SignInDTO.class)
                .flatMap(credentials -> authUseCase.signIn(credentials.email(), credentials.password())
                        .flatMap(auth -> {
                            ResponseApiDto<Auth> response = ResponseApiDto.<Auth>builder()
                                    .code(ResponseCode.USER_AUTHENTICATED.getCodeValue())
                                    .message(ResponseCode.USER_AUTHENTICATED.getDefaultMessage())
                                    .data(auth)
                                    .build();

                            return ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(response);
                        }));
    }

    @Operation(
            operationId = "isValid",
            summary = "String token",
            description = "Validate an authentication token",
            requestBody = @RequestBody(
                    required = true,
                    description = "token",
                    content = @Content(schema = @Schema(implementation = ValidateTokenDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "is valid successful"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Invalid token"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Missing or invalid fields"
                    )
            }
    )

    public Mono<ServerResponse> validateToken(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ValidateTokenDTO.class)
                .flatMap(tokenDTO -> authUseCase.validateToken(tokenDTO.token()))
                .map(validationTokenMapper::toResponse)
                .flatMap(user -> {
                    ResponseApiDto<ValidationTokenResponseDTO> response = ResponseApiDto.<ValidationTokenResponseDTO>builder()
                            .code(ResponseCode.TOKEN_GENERATED.getCodeValue())
                            .message(ResponseCode.TOKEN_GENERATED.getDefaultMessage())
                            .data(user)
                            .build();

                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                });
    }
}
