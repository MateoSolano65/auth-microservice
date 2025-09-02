package co.com.pragma.api.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ValidatorDTOTest {

    @Mock
    Validator validator;

    @InjectMocks
    ValidatorDTO validatorDTO;

    @Test
    void shouldReturnObjectWhenValid() {
        UserDtoStub obj = new UserDtoStub("john@doe.com");
        when(validator.validate(obj)).thenReturn(Collections.emptySet());

        Mono<UserDtoStub> mono = validatorDTO.validate(obj);

        verifyNoInteractions(validator);

        StepVerifier.create(mono)
                .expectNext(obj)
                .verifyComplete();

        verify(validator).validate(obj);
        verifyNoMoreInteractions(validator);
    }

    @Test
    void shouldEmitConstraintViolationExceptionWhenInvalid() {
        UserDtoStub obj = new UserDtoStub("bad-email");
        @SuppressWarnings("unchecked")
        ConstraintViolation<UserDtoStub> violation = mock(ConstraintViolation.class);
        @SuppressWarnings("unchecked")
        Set<ConstraintViolation<UserDtoStub>> violations = (Set) Collections.singleton(violation);
        when(validator.validate(obj)).thenReturn(violations);

        Mono<UserDtoStub> mono = validatorDTO.validate(obj);

        StepVerifier.create(mono)
                .expectErrorSatisfies(t -> {
                    ConstraintViolationException e = (ConstraintViolationException) t;
                    assertEquals(1, e.getConstraintViolations().size());
                })
                .verify();

        verify(validator).validate(obj);
        verifyNoMoreInteractions(validator);
    }

    @Test
    void shouldValidateOnEachSubscription() {
        UserDtoStub obj = new UserDtoStub("john@doe.com");
        when(validator.validate(obj)).thenReturn(Collections.emptySet());

        Mono<UserDtoStub> mono = validatorDTO.validate(obj);

        StepVerifier.create(mono).expectNext(obj).verifyComplete();
        StepVerifier.create(mono).expectNext(obj).verifyComplete();

        verify(validator, times(2)).validate(obj);
        verifyNoMoreInteractions(validator);
    }

    static class UserDtoStub {
        private final String email;
        UserDtoStub(String email) { this.email = email; }
        public String getEmail() { return email; }
    }
}
