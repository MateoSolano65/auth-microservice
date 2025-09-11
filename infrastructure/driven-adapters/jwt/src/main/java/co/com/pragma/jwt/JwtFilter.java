package co.com.pragma.jwt;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter implements WebFilter {
    public static final String TOKEN_ATTR = "token";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        if (PublicRoutes.isPublic(path)) {
            return chain.filter(exchange);
        }

        String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null) {
            return Mono.error(new Throwable("no token was found"));
        }

        if(!auth.startsWith(BEARER_PREFIX)){
            return Mono.error(new Throwable("invalid auth"));
        }
        String token = auth.replace(BEARER_PREFIX, "");
        exchange.getAttributes().put(TOKEN_ATTR, token);
        return chain.filter(exchange);
    }
}
