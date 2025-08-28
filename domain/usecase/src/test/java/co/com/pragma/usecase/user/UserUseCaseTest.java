package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserGateway;
import co.com.pragma.model.exception.ResourceConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import java.util.Arrays;
import java.util.List;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {
    @InjectMocks
    UserUseCase userUseCase;

    @Mock
    UserGateway userGateway;
    
    @Test
    void shouldCreateUserSuccessfully() {
        User user = User.builder()
                .email("test@example.com")
                .documentNumber("112233")
                .name("John")
                .lastName("Doe")
                .build();

        when(userGateway.existUserByDocumentNumber(user.getDocumentNumber())).thenReturn(Mono.just(false));
        when(userGateway.existUserByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(userGateway.saveUser(any(User.class))).thenReturn(Mono.just(user.toBuilder().id(1L).build()));

        Mono<User> result = userUseCase.createUser(user);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getId().equals(1L))
                .verifyComplete();
    }
    
    @Test
    void shouldThrowExceptionWhenBothEmailAndDocumentNumberExist() {
        User user = User.builder()
                .email("test@example.com")
                .documentNumber("112233")
                .name("John")
                .lastName("Doe")
                .build();
                
        List<String> errorList = Arrays.asList(
                userUseCase.emailAlreadyExists, 
                userUseCase.documentNumberAlreadyExists
        );

        when(userGateway.existUserByDocumentNumber(user.getDocumentNumber())).thenReturn(Mono.just(true));
        when(userGateway.existUserByEmail(user.getEmail())).thenReturn(Mono.just(true));

        Mono<User> result = userUseCase.createUser(user);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResourceConflictException &&
                        throwable.getMessage().equals(String.join(", ", errorList))
                )
                .verify();
    }

    @Test
void shouldThrowExceptionWhenOnlyEmailExists() {
    User user = User.builder()
            .email("test@example.com")
            .documentNumber("112233")
            .build();
    
    when(userGateway.existUserByDocumentNumber(user.getDocumentNumber())).thenReturn(Mono.just(false));
    when(userGateway.existUserByEmail(user.getEmail())).thenReturn(Mono.just(true));
    
    Mono<User> result = userUseCase.createUser(user);
    
    StepVerifier.create(result)
            .expectErrorMatches(throwable ->
                    throwable instanceof ResourceConflictException &&
                    throwable.getMessage().contains(userUseCase.emailAlreadyExists)
            )
            .verify();
}

@Test
void shouldThrowExceptionWhenOnlyDocumentNumberExists() {
    User user = User.builder()
            .email("test@example.com")
            .documentNumber("112233")
            .build();
    
    when(userGateway.existUserByDocumentNumber(user.getDocumentNumber())).thenReturn(Mono.just(true));
    when(userGateway.existUserByEmail(user.getEmail())).thenReturn(Mono.just(false));
    
    Mono<User> result = userUseCase.createUser(user);
    
    StepVerifier.create(result)
            .expectErrorMatches(throwable ->
                    throwable instanceof ResourceConflictException &&
                    throwable.getMessage().contains(userUseCase.documentNumberAlreadyExists)
            )
            .verify();
}

@Test
void shouldGetAllUsers() {
    User user1 = User.builder().id(1L).email("user1@example.com").build();
    User user2 = User.builder().id(2L).email("user2@example.com").build();
    
    when(userGateway.findAll()).thenReturn(Flux.just(user1, user2));
    
    Flux<User> result = userUseCase.getAllUsers();
    
    StepVerifier.create(result)
            .expectNext(user1)
            .expectNext(user2)
            .verifyComplete();
}
}