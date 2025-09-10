package co.com.pragma.api;

import co.com.pragma.api.dto.request.SignInDTO;
import co.com.pragma.api.dto.request.ValidateTokenDTO;
import co.com.pragma.api.handler.AuthHandler;
import co.com.pragma.api.handler.UserHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AuthRouterRest {

    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/login",
                    method = RequestMethod.POST,
                    beanClass = AuthHandler.class,
                    beanMethod = "listenSignIn"
            ),
            @RouterOperation(
                    path = "/api/v1/token",
                    method = RequestMethod.POST,
                    beanClass = AuthHandler.class,
                    beanMethod = "validateToken"
            )
    })
    @Bean
    public RouterFunction<ServerResponse> routerAuthFunction(AuthHandler authHandler) {
        return route()
                .POST("/api/v1/login", authHandler::listenSignIn)
                .POST("/api/v1/token", authHandler::validateToken)
                .build();
    }
}
