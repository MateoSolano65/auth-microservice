// package co.com.pragma.api;

// import co.com.pragma.api.dto.ResponseApiDto;
// import co.com.pragma.api.dto.UserDto;
// import co.com.pragma.api.handler.UserHandler;
// import co.com.pragma.api.mapper.UserMapper;
// import co.com.pragma.api.validator.ValidatorDTO;
// import co.com.pragma.model.user.User;
// import co.com.pragma.usecase.user.UserUseCase;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.reactive.server.WebTestClient;
// import org.springframework.web.reactive.function.server.RouterFunction;
// import org.springframework.web.reactive.function.server.ServerResponse;
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;

// @ExtendWith(MockitoExtension.class)
// class UserRouterRestTest {

//     @Mock
//     UserUseCase userUseCase;

//     @Mock
//     UserMapper userMapper;

//     @Mock
//     ValidatorDTO validatorDTO;

//     @InjectMocks
//     UserHandler userHandler;

//     WebTestClient webTestClient;

//     UserDto userDto;
//     User user;

//     @BeforeEach
//     void setUp() {
//         UserRouterRest router = new UserRouterRest();
//         RouterFunction<ServerResponse> rf = router.routerFunction(userHandler);
//         webTestClient = WebTestClient.bindToRouterFunction(rf).configureClient().baseUrl("/").build();

//         userDto = new UserDto();
//         userDto.setId(1L);
//         userDto.setName("John");
//         userDto.setLastName("Doe");
//         userDto.setEmail("john.doe@example.com");
//         userDto.setDocumentNumber("123456789");

//         user = User.builder()
//                 .id(1L)
//                 .name("John")
//                 .lastName("Doe")
//                 .email("john.doe@example.com")
//                 .documentNumber("123456789")
//                 .build();
//     }

//     @Test
//     void createUserPost_shouldReturn201WithWrappedDto() {
//         when(validatorDTO.validate(any(UserDto.class))).thenReturn(Mono.just(userDto));
//         when(userMapper.toUserDomain(any(UserDto.class))).thenReturn(user);
//         when(userUseCase.create(any(User.class))).thenReturn(Mono.just(user));
//         when(userMapper.toUserDto(any(User.class))).thenReturn(userDto);

//         webTestClient.post()
//                 .uri("/api/users")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .bodyValue(userDto)
//                 .exchange()
//                 .expectStatus().isCreated()
//                 .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
//                 .expectBody()
//                 .jsonPath("$.status").isEqualTo(201)
//                 .jsonPath("$.message").isEqualTo("Created")
//                 .jsonPath("$.data.id").isEqualTo(1)
//                 .jsonPath("$.data.email").isEqualTo("john.doe@example.com")
//                 .jsonPath("$.data.documentNumber").isEqualTo("123456789");
//     }

//     @Test
//     void createUserPost_shouldReturn5xxOnValidationError() {
//         when(validatorDTO.validate(any(UserDto.class))).thenReturn(Mono.error(new IllegalArgumentException("Validation failed")));

//         webTestClient.post()
//                 .uri("/api/users")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .bodyValue(new UserDto())
//                 .exchange()
//                 .expectStatus().is5xxServerError();
//     }

//     @Test
//     void createUserPost_shouldReturn5xxOnUseCaseError() {
//         when(validatorDTO.validate(any(UserDto.class))).thenReturn(Mono.just(userDto));
//         when(userMapper.toUserDomain(any(UserDto.class))).thenReturn(user);
//         when(userUseCase.create(any(User.class))).thenReturn(Mono.error(new RuntimeException("boom")));

//         webTestClient.post()
//                 .uri("/api/users")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .bodyValue(userDto)
//                 .exchange()
//                 .expectStatus().is5xxServerError();
//     }

//     @Test
//     void getAllUsers_shouldReturn200WithWrappedList() {
//         when(userUseCase.getAllUsers()).thenReturn(Flux.just(user));
//         when(userMapper.toUserDto(any(User.class))).thenReturn(userDto);

//         webTestClient.get()
//                 .uri("/api/users")
//                 .accept(MediaType.APPLICATION_JSON)
//                 .exchange()
//                 .expectStatus().isOk()
//                 .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
//                 .expectBody()
//                 .jsonPath("$.status").isEqualTo(200)
//                 .jsonPath("$.message").isEqualTo("OK")
//                 .jsonPath("$.data.length()").isEqualTo(1)
//                 .jsonPath("$.data[0].id").isEqualTo(1)
//                 .jsonPath("$.data[0].email").isEqualTo("john.doe@example.com");
//     }

//     @Test
//     void getAllUsers_shouldReturn200WithEmptyList() {
//         when(userUseCase.getAllUsers()).thenReturn(Flux.empty());

//         webTestClient.get()
//                 .uri("/api/users")
//                 .accept(MediaType.APPLICATION_JSON)
//                 .exchange()
//                 .expectStatus().isOk()
//                 .expectBody(ResponseApiDto.class)
//                 .consumeWith(r -> {});
//     }
// }
