package co.com.pragma.api.exceptions;

import java.util.HashMap;

import co.com.pragma.model.exception.BusinessRuleViolationException;
import co.com.pragma.model.exception.ResourceConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {
    
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private static final HashMap<Class<?>, HttpStatus> httpStatusCodes = new HashMap<>();

    public GlobalExceptionHandler(ErrorAttributes errorAttributes, ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        httpStatusCodes.put(BusinessRuleViolationException.class, HttpStatus.BAD_REQUEST);
        httpStatusCodes.put(ResourceConflictException.class, HttpStatus.CONFLICT);
    }

    private Mono<ServerResponse> errorResponse(ServerRequest request) {
        Throwable throwable = this.getError(request);
        HttpStatus responseCode = httpStatusCode((Exception) throwable);
        String message = throwable.getMessage();
        ErrorResponse error = new ErrorResponse(responseCode.value(), message);

        if (!(throwable instanceof BusinessRuleViolationException) && !Exceptions.isMultiple(throwable)
                && !(throwable instanceof WebExchangeBindException)
                && !(throwable instanceof ResourceConflictException)) {
            responseCode = throwable instanceof ResponseStatusException responseStatusException ?
                    HttpStatus.valueOf(responseStatusException.getStatusCode().value()) :
                    HttpStatus.INTERNAL_SERVER_ERROR;
            error = new ErrorResponse();

            logger.error(message, throwable);
        }
        return ServerResponse.status(responseCode)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }

    private HttpStatus httpStatusCode(Exception throwable) {
        HttpStatus statusCode = httpStatusCodes.get(throwable.getClass());
        return statusCode == null ? HttpStatus.BAD_REQUEST : statusCode;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::errorResponse);
    }
}
