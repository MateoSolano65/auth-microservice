package co.com.pragma.api;

import co.com.pragma.api.dto.UserDto;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.api.validator.ValidatorDTO;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {UserRouterRest.class, UserHandler.class})
@WebFluxTest
class UserRouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserUseCase userUseCase;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private ValidatorDTO validatorDTO;

    private UserDto userDto;
    private User user;
    private List<UserDto> userDtoList;
    private List<User> userList;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setDocumentNumber("123456789");

        user = User.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .build();

        userDtoList = new ArrayList<>();
        userDtoList.add(userDto);

        userList = new ArrayList<>();
        userList.add(user);
    }

    @Test
    void testCreateUserSuccessful() {

        when(validatorDTO.validate(any(UserDto.class))).thenReturn(Mono.just(userDto));
        when(userMapper.toEntity(any(UserDto.class))).thenReturn(user);
        when(userUseCase.createUser(any(User.class))).thenReturn(Mono.just(user));
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);


        webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserDto.class)
                .isEqualTo(userDto);
    }

    @Test
    void testGetAllUsersSuccessful() {

        when(userUseCase.getAllUsers()).thenReturn(Flux.fromIterable(userList));
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);


        webTestClient.get()
                .uri("/api/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserDto.class)
                .hasSize(1)
                .contains(userDto);
    }
    
    @Test
    void testCreateUserValidationError() {

        UserDto invalidUserDto = new UserDto();
        when(validatorDTO.validate(any(UserDto.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Validation failed")));


        webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidUserDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }
    
    @Test
    void testCreateUserBusinessError() {

        UserDto duplicateUserDto = new UserDto();
        duplicateUserDto.setEmail("existing@example.com");
        duplicateUserDto.setDocumentNumber("123456");
        
        when(validatorDTO.validate(any(UserDto.class))).thenReturn(Mono.just(duplicateUserDto));
        when(userMapper.toEntity(any(UserDto.class))).thenReturn(user);
        when(userUseCase.createUser(any(User.class)))
                .thenReturn(Mono.error(new RuntimeException("User already exists")));


        webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(duplicateUserDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }
    
    @Test
    void testGetAllUsersEmpty() {

        when(userUseCase.getAllUsers()).thenReturn(Flux.empty());


        webTestClient.get()
                .uri("/api/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserDto.class)
                .hasSize(0);
    }
}