package co.com.pragma.usecase.user;

import co.com.pragma.model.auth.gateways.PasswordEncoderGateway;
import co.com.pragma.model.exception.BusinessRuleViolationException;
import co.com.pragma.model.exception.ResourceConflictException;
import co.com.pragma.model.response.ResponseCode;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserUseCaseTest {

    @Mock
    UserGateway userGateway;
    
    @Mock
    PasswordEncoderGateway passwordEncoder;

    @InjectMocks
    UserUseCase userUseCase;

    @Test
    void shouldCreateSuccessfully() {
        User input = User.builder()
                .email("test@example.com")
                .documentNumber("112233")
                .name("John")
                .lastName("Doe")
                .password("password123")
                .build();
        
        User userWithEncodedPassword = input.toBuilder()
                .password("encodedPassword")
                .build();
                
        User saved = userWithEncodedPassword.toBuilder().id(1L).build();
        
        when(userGateway.existUserByEmailAndDocument("test@example.com", "112233")).thenReturn(Mono.just(false));
        when(passwordEncoder.encode("password123")).thenReturn(Mono.just("encodedPassword"));
        when(userGateway.saveUser(any(User.class))).thenReturn(Mono.just(saved));

        Mono<User> result = userUseCase.create(input);

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getId().equals(1L)
                        && u.getEmail().equals("test@example.com")
                        && u.getDocumentNumber().equals("112233"))
                .verifyComplete();
                
        verify(userGateway).existUserByEmailAndDocument("test@example.com", "112233");
        verify(passwordEncoder).encode("password123");
        verify(userGateway).saveUser(argThat(user -> 
                user.getEmail().equals("test@example.com") &&
                user.getPassword().equals("encodedPassword")));
        verifyNoMoreInteractions(userGateway);
        verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        User input = User.builder()
                .email("test@example.com")
                .documentNumber("112233")
                .name("John")
                .lastName("Doe")
                .password("password123")
                .build();
        when(userGateway.existUserByEmailAndDocument("test@example.com", "112233")).thenReturn(Mono.just(true));

        Mono<User> result = userUseCase.create(input);

        StepVerifier.create(result)
                .expectErrorMatches(t -> t instanceof ResourceConflictException
                        && "User already exists".equals(t.getMessage()))
                .verify();
        verify(userGateway).existUserByEmailAndDocument("test@example.com", "112233");
        verify(userGateway, never()).saveUser(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
        verifyNoMoreInteractions(userGateway);
        verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    void shouldGetAllUsers() {
        User user1 = User.builder().id(1L).email("user1@example.com").documentNumber("1").build();
        User user2 = User.builder().id(2L).email("user2@example.com").documentNumber("2").build();
        when(userGateway.findAll()).thenReturn(Flux.just(user1, user2));

        Flux<User> result = userUseCase.getAllUsers();

        StepVerifier.create(result)
                .expectNext(user1)
                .expectNext(user2)
                .verifyComplete();
        verify(userGateway).findAll();
        verifyNoMoreInteractions(userGateway);
        verifyNoInteractions(passwordEncoder);
    }
    
    @Test
    void shouldValidateExistsByEmailAndDocument() {
        String email = "test@example.com";
        String documentNumber = "112233";
        when(userGateway.existUserByEmailAndDocument(email, documentNumber)).thenReturn(Mono.just(true));

        Mono<Boolean> result = userUseCase.validateExistsByEmailAndDocument(email, documentNumber);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
        verify(userGateway).existUserByEmailAndDocument(email, documentNumber);
        verifyNoMoreInteractions(userGateway);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotExistByEmailAndDocument() {
        String email = "nonexistent@example.com";
        String documentNumber = "999999";
        when(userGateway.existUserByEmailAndDocument(email, documentNumber)).thenReturn(Mono.just(false));

        Mono<Boolean> result = userUseCase.validateExistsByEmailAndDocument(email, documentNumber);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
        verify(userGateway).existUserByEmailAndDocument(email, documentNumber);
        verifyNoMoreInteractions(userGateway);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldFindUserByEmail() {
        String email = "test@example.com";
        User expectedUser = User.builder()
                .id(1L)
                .email(email)
                .documentNumber("112233")
                .name("John")
                .lastName("Doe")
                .build();
        when(userGateway.existUserByEmail(email)).thenReturn(Mono.just(expectedUser));

        Mono<User> result = userUseCase.existUserByEmail(email);

        StepVerifier.create(result)
                .expectNext(expectedUser)
                .verifyComplete();
        verify(userGateway).existUserByEmail(email);
        verifyNoMoreInteractions(userGateway);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldReturnEmptyWhenUserDoesNotExistByEmail() {
        String email = "nonexistent@example.com";
        when(userGateway.existUserByEmail(email)).thenReturn(Mono.empty());

        Mono<User> result = userUseCase.existUserByEmail(email);

        StepVerifier.create(result)
                .verifyComplete();
        verify(userGateway).existUserByEmail(email);
        verifyNoMoreInteractions(userGateway);
        verifyNoInteractions(passwordEncoder);
    }
    
    @Test
    void shouldThrowBusinessRuleViolationExceptionWhenEmailDocumentValidationFails() {
        String email = "test@example.com";
        String documentNumber = "112233";
        
        when(userGateway.existUserByEmailAndDocument(email, documentNumber))
            .thenReturn(Mono.error(new BusinessRuleViolationException(ResponseCode.CONFLICT)));

        Mono<Boolean> result = userUseCase.validateExistsByEmailAndDocument(email, documentNumber);

        StepVerifier.create(result)
                .expectErrorMatches(t -> t instanceof BusinessRuleViolationException
                        && ((BusinessRuleViolationException) t).code().equals("API-409"))
                .verify();
                
        verify(userGateway).existUserByEmailAndDocument(email, documentNumber);
        verifyNoMoreInteractions(userGateway);
        verifyNoInteractions(passwordEncoder);
    }
}
