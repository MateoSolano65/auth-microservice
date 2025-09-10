package co.com.pragma.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

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
public class OpenApiConfig {
}
