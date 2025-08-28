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
import org.springframework.data.domain.Example;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @InjectMocks
    UserRepositoryAdapter repositoryAdapter;

    @Mock
    UserRepository repository;

    @Mock
    ObjectMapper mapper;
    
    @Mock
    TransactionalOperator transactionalOperator;
    
    private User testUser;
    private UserData testUserData;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .phoneNumber("1234567890")
                .role("USER")
                .build();
                
        testUserData = new UserData();
        testUserData.setId(1L);
        testUserData.setName("John");
        testUserData.setLastName("Doe");
        testUserData.setEmail("john.doe@example.com");
        testUserData.setDocumentNumber("123456789");
        testUserData.setPhoneNumber("1234567890");
        testUserData.setRole("USER");
    }

    @Test
    void shouldSaveUser() {
        // Arrange
        when(mapper.map(testUser, UserData.class)).thenReturn(testUserData);
        when(repository.save(testUserData)).thenReturn(Mono.just(testUserData));
        when(mapper.map(testUserData, User.class)).thenReturn(testUser);
        
        // Mock the transactional behavior
        when(transactionalOperator.transactional(any(Mono.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        Mono<User> result = repositoryAdapter.saveUser(testUser);
        
        // Assert
        StepVerifier.create(result)
                .expectNext(testUser)
                .verifyComplete();
    }
    
    @Test
    void shouldCheckIfUserExistsByDocumentNumber() {
        // Arrange
        String documentNumber = "123456789";
        when(repository.existsByDocumentNumber(documentNumber)).thenReturn(Mono.just(true));
        
        // Act
        Mono<Boolean> result = repositoryAdapter.existUserByDocumentNumber(documentNumber);
        
        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }
    
    @Test
    void shouldCheckIfUserExistsByEmail() {
        // Arrange
        String email = "john.doe@example.com";
        when(repository.existsByEmail(email)).thenReturn(Mono.just(false));
        
        // Act
        Mono<Boolean> result = repositoryAdapter.existUserByEmail(email);
        
        // Assert
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }
    
    @Test
    void shouldFindAllUsers() {
        // Arrange
        when(repository.findAll()).thenReturn(Flux.just(testUserData));
        when(mapper.map(testUserData, User.class)).thenReturn(testUser);
        
        // Act
        Flux<User> result = repositoryAdapter.findAll();
        
        // Assert
        StepVerifier.create(result)
                .expectNext(testUser)
                .verifyComplete();
    }
    
    @Test
    void shouldFindByIdUser() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Mono.just(testUserData));
        when(mapper.map(testUserData, User.class)).thenReturn(testUser);
        
        // Act
        Mono<User> result = repositoryAdapter.findById(1L);
        
        // Assert
        StepVerifier.create(result)
                .expectNext(testUser)
                .verifyComplete();
    }
    
    @Test
    void shouldFindByExample() {
        // Arrange
        User filterUser = User.builder().email("john.doe@example.com").build();
        UserData filterUserData = new UserData();
        filterUserData.setEmail("john.doe@example.com");
        
        when(mapper.map(filterUser, UserData.class)).thenReturn(filterUserData);
        when(repository.findAll(any(Example.class))).thenReturn(Flux.just(testUserData));
        when(mapper.map(testUserData, User.class)).thenReturn(testUser);
        
        // Act
        Flux<User> result = repositoryAdapter.findByExample(filterUser);
        
        // Assert
        StepVerifier.create(result)
                .expectNext(testUser)
                .verifyComplete();
    }
}