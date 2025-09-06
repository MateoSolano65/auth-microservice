package co.com.pragma.jwt;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@AllArgsConstructor
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                                .pathMatchers(
                                        "/v3/api-docs/",
                                        "/swagger-ui.html",
                                        "/swagger-ui/",
                                        "/webjars/swagger-ui/**"
//                                        "/api/users"
                                ).permitAll()
//                        .pathMatchers(
//                                "/api/v1/solicitudes"
//
//                        ).hasAnyRole("CLIENT")
                                .anyExchange().authenticated()
                )
//                .addFilterAfter(jwtFilter, SecurityWebFiltersOrder.FIRST)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .build();
    }
}
