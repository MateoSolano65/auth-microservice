package co.com.pragma.usecase.user;

import co.com.pragma.model.exception.ResourceConflictException;
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

    @InjectMocks
    UserUseCase userUseCase;

    @Test
    void shouldCreateSuccessfully() {
        User input = User.builder()
                .email("test@example.com")
                .documentNumber("112233")
                .name("John")
                .lastName("Doe")
                .build();
        User saved = input.toBuilder().id(1L).build();
        when(userGateway.existUserByEmailAndDocument("test@example.com", "112233")).thenReturn(Mono.just(false));
        when(userGateway.saveUser(any(User.class))).thenReturn(Mono.just(saved));

        Mono<User> result = userUseCase.create(input);

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getId().equals(1L)
                        && u.getEmail().equals("test@example.com")
                        && u.getDocumentNumber().equals("112233"))
                .verifyComplete();
        verify(userGateway).existUserByEmailAndDocument("test@example.com", "112233");
        verify(userGateway).saveUser(input);
        verifyNoMoreInteractions(userGateway);
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        User input = User.builder()
                .email("test@example.com")
                .documentNumber("112233")
                .name("John")
                .lastName("Doe")
                .build();
        when(userGateway.existUserByEmailAndDocument("test@example.com", "112233")).thenReturn(Mono.just(true));

        Mono<User> result = userUseCase.create(input);

        StepVerifier.create(result)
                .expectErrorMatches(t -> t instanceof ResourceConflictException
                        && "User already exists".equals(t.getMessage()))
                .verify();
        verify(userGateway).existUserByEmailAndDocument("test@example.com", "112233");
        verify(userGateway, never()).saveUser(any(User.class));
        verifyNoMoreInteractions(userGateway);
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
    }
}
