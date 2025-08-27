package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserGateway;
import co.com.pragma.r2dbc.entities.UserData;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryAdapter extends ReactiveAdapterOperations< User, UserData, Long, UserRepository > implements UserGateway {

    private final TransactionalOperator transactionalOperator;

    public UserRepositoryAdapter(UserRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<User> saveUser(User user) {
        return this.save(user)
            .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Boolean> existUserByDocumentNumber(String documentNumber) {
        return repository.existsByDocumentNumber(documentNumber);
    }

    @Override
    public Mono<Boolean> existUserByEmail(String email) {
        return repository.existsByEmail(email);
    }
}
