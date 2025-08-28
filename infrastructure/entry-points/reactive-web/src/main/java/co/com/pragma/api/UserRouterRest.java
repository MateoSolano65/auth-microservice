package co.com.pragma.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.pragma.api.dto.UserDto;
import co.com.pragma.api.exceptions.ErrorResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UserRouterRest {

    @RouterOperations({
            @RouterOperation(
                    path = "/api/users",
                    method = RequestMethod.POST,
                    beanClass = UserHandler.class,        // <—
                    beanMethod = "createUserPost",        // <—
                    operation = @Operation(
                            operationId = "createUser",   // <—
                            summary = "Crear un nuevo usuario",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    content = @Content(schema = @Schema(implementation = UserDto.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                                            content = @Content(schema = @Schema(implementation = UserDto.class))),
                                    @ApiResponse(responseCode = "400", description = "Datos inválidos",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "500", description = "Error interno",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/users",
                    method = RequestMethod.GET,
                    beanClass = UserHandler.class,
                    beanMethod = "getAllUsers",
                    operation = @Operation(
                            operationId = "getAllUsers",
                            summary = "Listar usuarios",
                            responses = { @ApiResponse(responseCode = "200",
                                    content = @Content(schema = @Schema(implementation = UserDto.class))) }
                    )
            )
    })
    @Bean
    public RouterFunction<ServerResponse> routerFunction(UserHandler userHandler) {
        return route(POST("/api/users"), userHandler::createUserPost)
                .andRoute(GET("/api/users"), userHandler::getAllUsers);
    }
}
