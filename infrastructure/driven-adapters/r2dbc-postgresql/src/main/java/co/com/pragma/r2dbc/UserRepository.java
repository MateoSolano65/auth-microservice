package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entities.UserData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<UserData, Long>, ReactiveQueryByExampleExecutor<UserData> {
    Mono<Boolean> existsByEmailAndDocumentNumber(String email, String documentNumber);
}
