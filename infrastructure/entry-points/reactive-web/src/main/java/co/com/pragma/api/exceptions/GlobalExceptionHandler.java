package co.com.pragma.api.exceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
  private static final List<Class<?>> businessExceptions = new ArrayList<>();

    public GlobalExceptionHandler(ErrorAttributes errorAttributes, ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
        
        httpStatusCodes.put(BusinessRuleViolationException.class, HttpStatus.BAD_REQUEST);
        httpStatusCodes.put(ResourceConflictException.class, HttpStatus.CONFLICT);
        httpStatusCodes.put(WebExchangeBindException.class, HttpStatus.BAD_REQUEST);
        
        businessExceptions.add(BusinessRuleViolationException.class);
        businessExceptions.add(ResourceConflictException.class);
        businessExceptions.add(WebExchangeBindException.class);
    }

    private Mono<ServerResponse> errorResponse(ServerRequest request) {
        Throwable throwable = this.getError(request);
        String message = throwable.getMessage();
        HttpStatus responseCode;
        
        if (throwable instanceof ResponseStatusException responseStatusException) {
            responseCode = HttpStatus.valueOf(responseStatusException.getStatusCode().value());
        } else if (httpStatusCodes.containsKey(throwable.getClass())) {
            responseCode = httpStatusCodes.get(throwable.getClass());
        } else if (Exceptions.isMultiple(throwable)) {
            responseCode = HttpStatus.BAD_REQUEST;
        } else {
            responseCode = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        ErrorResponse error = new ErrorResponse(responseCode.value(), message);
        
        boolean isBusinessException = businessExceptions.stream()
            .anyMatch(exClass -> exClass.isInstance(throwable)) || Exceptions.isMultiple(throwable);
            
        if (!isBusinessException) {
            logger.error(message, throwable);
        }
        
        return ServerResponse.status(responseCode)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(error);
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::errorResponse);
    }
}
