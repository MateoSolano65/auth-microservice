package co.com.pragma.api.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityHeadersConfigTest {

    @InjectMocks
    SecurityHeadersConfig filter;

    @Mock
    ServerWebExchange exchange;

    @Mock
    ServerHttpResponse response;

    @Mock
    WebFilterChain chain;

    @Test
    void shouldSetSecurityHeadersAndContinueChain() {
        HttpHeaders headers = new HttpHeaders();
        when(exchange.getResponse()).thenReturn(response);
        when(response.getHeaders()).thenReturn(headers);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();
        assertEquals("default-src 'self'; frame-ancestors 'self'; form-action 'self'", headers.getFirst("Content-Security-Policy"));
        assertEquals("max-age=31536000;", headers.getFirst("Strict-Transport-Security"));
        assertEquals("nosniff", headers.getFirst("X-Content-Type-Options"));
        assertEquals("", headers.getFirst("Server"));
        assertEquals("no-store", headers.getFirst("Cache-Control"));
        assertEquals("no-cache", headers.getFirst("Pragma"));
        assertEquals("strict-origin-when-cross-origin", headers.getFirst("Referrer-Policy"));
        verify(chain).filter(exchange);
        verifyNoMoreInteractions(chain);
    }

    @Test
    void shouldOverrideExistingHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Security-Policy", "old");
        headers.set("Strict-Transport-Security", "old");
        headers.set("X-Content-Type-Options", "old");
        headers.set("Server", "old");
        headers.set("Cache-Control", "old");
        headers.set("Pragma", "old");
        headers.set("Referrer-Policy", "old");
        when(exchange.getResponse()).thenReturn(response);
        when(response.getHeaders()).thenReturn(headers);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();
        assertEquals("default-src 'self'; frame-ancestors 'self'; form-action 'self'", headers.getFirst("Content-Security-Policy"));
        assertEquals("max-age=31536000;", headers.getFirst("Strict-Transport-Security"));
        assertEquals("nosniff", headers.getFirst("X-Content-Type-Options"));
        assertEquals("", headers.getFirst("Server"));
        assertEquals("no-store", headers.getFirst("Cache-Control"));
        assertEquals("no-cache", headers.getFirst("Pragma"));
        assertEquals("strict-origin-when-cross-origin", headers.getFirst("Referrer-Policy"));
        verify(chain).filter(exchange);
        verifyNoMoreInteractions(chain);
    }
}
