package co.com.pragma.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API de Usuarios",
                version = "1.0",
                description = "API para la gestión de usuarios del sistema",
                contact = @Contact(
                        name = "CrediYa",
                        email = "soporte@crediya.com.co"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = {
                @Server(
                        url = "/",
                        description = "Servidor de desarrollo"
                )
        }
)

public class UserRouterRest {

    @RouterOperations({
        @RouterOperation(
            path = "/api/users",
            method = RequestMethod.POST,
            beanClass = UserHandler.class,
            beanMethod = "createUserPost"
        ),
        @RouterOperation(
            path = "/api/users",
            method = RequestMethod.GET,
            beanClass = UserHandler.class,
            beanMethod = "getAllUsers"
        ),
        @RouterOperation(
            path = "/api/users/validate",
            method = RequestMethod.GET,
            beanClass = UserHandler.class,
            beanMethod = "validateUserExists"
        )
    })
    @Bean
    public RouterFunction<ServerResponse> routerFunction(UserHandler userHandler) {
        return route(POST("/api/users"), userHandler::createUserPost)
                .andRoute(GET("/api/users"), userHandler::getAllUsers)
                .andRoute(GET("/api/users/validate"), userHandler::validateUserExists);
    }
}
