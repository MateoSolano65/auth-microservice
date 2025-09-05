package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.entities.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryAdapterTest {

    @Mock
    UserRepository repository;

    @Mock
    ObjectMapper mapper;

    @Mock
    TransactionalOperator transactionalOperator;

    @InjectMocks
    UserRepositoryAdapter adapter;

    User user;
    UserData userData;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .phoneNumber("1234567890")
                .role("USER")
                .build();

        userData = new UserData();
        userData.setId(1L);
        userData.setName("John");
        userData.setLastName("Doe");
        userData.setEmail("john.doe@example.com");
        userData.setDocumentNumber("123456789");
        userData.setPhoneNumber("1234567890");
        userData.setRole("USER");
    }

    @Test
    void shouldSaveUserTransactional() {
        when(mapper.map(user, UserData.class)).thenReturn(userData);
        when(repository.save(userData)).thenReturn(Mono.just(userData));
        when(mapper.map(userData, User.class)).thenReturn(user);
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(i -> i.getArgument(0));

        Mono<User> result = adapter.saveUser(user);

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getId().equals(1L)
                        && u.getEmail().equals("john.doe@example.com")
                        && u.getDocumentNumber().equals("123456789"))
                .verifyComplete();

        verify(mapper).map(user, UserData.class);
        verify(repository).save(userData);
        verify(mapper).map(userData, User.class);
        verify(transactionalOperator).transactional(any(Mono.class));
        verifyNoMoreInteractions(repository, mapper, transactionalOperator);
    }

    @Test
    void shouldCheckExistByEmailOrDocument() {
        when(repository.existsByEmailOrDocumentNumber("john.doe@example.com", "123456789")).thenReturn(Mono.just(true));

        Mono<Boolean> result = adapter.existUserByEmailAndDocument("john.doe@example.com", "123456789");

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(repository).existsByEmailOrDocumentNumber("john.doe@example.com", "123456789");
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper, transactionalOperator);
    }
}
