package co.com.pragma.usecase.auth;

import co.com.pragma.model.auth.Auth;
import co.com.pragma.model.auth.gateways.AuthProviderGateway;
import co.com.pragma.model.auth.gateways.PasswordEncoderGateway;
import co.com.pragma.model.exception.AuthenticationException;
import co.com.pragma.model.exception.BusinessRuleViolationException;
import co.com.pragma.model.response.ResponseCode;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private UserUseCase userUseCase;

    @Mock
    private AuthProviderGateway authProviderGateway;

    @Mock
    private PasswordEncoderGateway passwordEncoder;

    @InjectMocks
    private AuthUseCase authUseCase;

    @Test
    void shouldSignInSuccessfully() {
        String email = "test@example.com";
        String password = "password123";
        User user = User.builder()
                .id(1L)
                .email(email)
                .password("encodedPassword")
                .documentNumber("112233")
                .name("John")
                .lastName("Doe")
                .build();
                
        Auth expectedAuth = Auth.builder()
                .token("jwt-token")
                .build();
                
        when(userUseCase.existUserByEmail(email)).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(Mono.just(true));
        when(authProviderGateway.authenticate(user)).thenReturn(Mono.just(expectedAuth));

        Mono<Auth> result = authUseCase.signIn(email, password);
        
        StepVerifier.create(result)
                .expectNext(expectedAuth)
                .verifyComplete();
                
        verify(userUseCase).existUserByEmail(email);
        verify(passwordEncoder).matches(password, "encodedPassword");
        verify(authProviderGateway).authenticate(user);
        verifyNoMoreInteractions(userUseCase, passwordEncoder, authProviderGateway);
    }
    
    @Test
    void shouldFailSignInWithInvalidEmail() {
        String email = "nonexistent@example.com";
        String password = "password123";
        
        when(userUseCase.existUserByEmail(email)).thenReturn(Mono.empty());

        Mono<Auth> result = authUseCase.signIn(email, password);
        
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof AuthenticationException && 
                    "invalid credentials".equals(throwable.getMessage()))
                .verify();
                
        verify(userUseCase).existUserByEmail(email);
        verifyNoInteractions(passwordEncoder, authProviderGateway);
        verifyNoMoreInteractions(userUseCase);
    }
    
    @Test
    void shouldFailSignInWithInvalidPassword() {
        String email = "test@example.com";
        String password = "wrongPassword";
        User user = User.builder()
                .id(1L)
                .email(email)
                .password("encodedPassword")
                .documentNumber("112233")
                .name("John")
                .lastName("Doe")
                .build();
                
        when(userUseCase.existUserByEmail(email)).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(Mono.just(false));

        Mono<Auth> result = authUseCase.signIn(email, password);
        
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof AuthenticationException && 
                    "invalid credentials".equals(throwable.getMessage()))
                .verify();
                
        verify(userUseCase).existUserByEmail(email);
        verify(passwordEncoder).matches(password, "encodedPassword");
        verifyNoInteractions(authProviderGateway);
        verifyNoMoreInteractions(userUseCase, passwordEncoder);
    }
    
    @Test
    void shouldValidateTokenSuccessfully() {
        String token = "valid-jwt-token";
        String email = "test@example.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .password("encodedPassword")
                .documentNumber("112233")
                .name("John")
                .lastName("Doe")
                .build();
                
        when(authProviderGateway.validateToken(token)).thenReturn(Mono.just(true));
        when(authProviderGateway.getSubject(token)).thenReturn(Mono.just(email));
        when(userUseCase.existUserByEmail(email)).thenReturn(Mono.just(user));

        Mono<User> result = authUseCase.validateToken(token);
        
        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
                
        verify(authProviderGateway).validateToken(token);
        verify(authProviderGateway).getSubject(token);
        verify(userUseCase).existUserByEmail(email);
        verifyNoMoreInteractions(authProviderGateway, userUseCase);
        verifyNoInteractions(passwordEncoder);
    }
    
    @Test
    void shouldFailValidateTokenWhenInvalid() {
        String token = "invalid-jwt-token";
        
        when(authProviderGateway.validateToken(token)).thenReturn(Mono.just(false));

        Mono<User> result = authUseCase.validateToken(token);
        
        StepVerifier.create(result)
                .verifyComplete();
                
        verify(authProviderGateway).validateToken(token);
        verifyNoMoreInteractions(authProviderGateway);
        verifyNoInteractions(userUseCase, passwordEncoder);
    }
    
    @Test
    void shouldThrowBusinessRuleViolationExceptionWithInvalidToken() {
        String token = "expired-jwt-token";
        
        when(authProviderGateway.validateToken(token))
            .thenReturn(Mono.error(new BusinessRuleViolationException(ResponseCode.UNAUTHORIZED)));

        Mono<User> result = authUseCase.validateToken(token);
        
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof BusinessRuleViolationException && 
                    ((BusinessRuleViolationException) throwable).code().equals("API-401"))
                .verify();
                
        verify(authProviderGateway).validateToken(token);
        verifyNoMoreInteractions(authProviderGateway);
        verifyNoInteractions(userUseCase, passwordEncoder);
    }
}