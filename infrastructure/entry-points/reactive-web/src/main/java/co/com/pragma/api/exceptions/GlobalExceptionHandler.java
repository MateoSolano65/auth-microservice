package co.com.pragma.api.exceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;

import co.com.pragma.api.dto.ErrorInfoDto;
import co.com.pragma.api.dto.ResponseApiDto;
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
        httpStatusCodes.put(WebExchangeBindException.class, HttpStatus.UNPROCESSABLE_ENTITY);
        httpStatusCodes.put(ConstraintViolationException.class, HttpStatus.UNPROCESSABLE_ENTITY);
        
        businessExceptions.add(BusinessRuleViolationException.class);
        businessExceptions.add(ResourceConflictException.class);
        businessExceptions.add(WebExchangeBindException.class);
        businessExceptions.add(ConstraintViolationException.class);
    }

    private Mono<ServerResponse> errorResponse(ServerRequest request) {
        Throwable throwable = this.getError(request);
        String message = throwable.getMessage();
        HttpStatus responseStatus;
        
        if (throwable instanceof ResponseStatusException responseStatusException) {
            responseStatus = HttpStatus.valueOf(responseStatusException.getStatusCode().value());
        } else if (httpStatusCodes.containsKey(throwable.getClass())) {
            responseStatus = httpStatusCodes.get(throwable.getClass());
        } else if (Exceptions.isMultiple(throwable)) {
            responseStatus = HttpStatus.BAD_REQUEST;
        } else {
            responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        ResponseApiDto<Object> response = createErrorResponse(throwable, responseStatus);
        
        boolean isBusinessException = businessExceptions.stream()
            .anyMatch(exClass -> exClass.isInstance(throwable)) || Exceptions.isMultiple(throwable);
            
        if (!isBusinessException) {
            logger.error(message, throwable);
        }
        
        return ServerResponse.status(responseStatus)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(response);
    }

    private ResponseApiDto<Object> createErrorResponse(Throwable throwable, HttpStatus status) {
        ResponseApiDto.ResponseApiDtoBuilder<Object> responseBuilder = ResponseApiDto.builder()
            .status(status.value());

        if (throwable instanceof WebExchangeBindException validationEx) {
            // Spring WebExchangeBindException validation error
            Map<String, Object> meta = new HashMap<>();
            List<Map<String, String>> violations = validationEx.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> {
                    Map<String, String> violation = new HashMap<>();
                    violation.put("field", fieldError.getField());
                    violation.put("message", fieldError.getDefaultMessage());
                    return violation;
                })
                .collect(Collectors.toList());
            
            meta.put("violations", violations);
            
            return responseBuilder
                .error(ErrorInfoDto.builder()
                    .code("VALIDATION_ERROR")
                    .detail("Invalid request body")
                    .build())
                .meta(meta)
                .build();
        } else if (throwable instanceof ConstraintViolationException validationEx) {
            // Jakarta ConstraintViolationException validation error
            Map<String, Object> meta = new HashMap<>();
            List<Map<String, String>> violations = validationEx.getConstraintViolations()
                .stream()
                .map(violation -> {
                    Map<String, String> violationMap = new HashMap<>();
                    // Extract field name from path
                    String field = violation.getPropertyPath().toString();
                    // If the path contains nodes, get the last node as the field name
                    if (field.contains(".")) {
                        field = field.substring(field.lastIndexOf('.') + 1);
                    }
                    violationMap.put("field", field);
                    violationMap.put("message", violation.getMessage());
                    return violationMap;
                })
                .collect(Collectors.toList());
            
            meta.put("violations", violations);
            
            return responseBuilder
                .error(ErrorInfoDto.builder()
                    .code("VALIDATION_ERROR")
                    .detail("Invalid request body")
                    .build())
                .meta(meta)
                .build();
        } else if (throwable instanceof ResourceConflictException) {
            // Resource conflict
            return responseBuilder
                .error(ErrorInfoDto.builder()
                    .code("RESOURCE_CONFLICT")
                    .detail(throwable.getMessage())
                    .build())
                .build();
        } else if (throwable instanceof BusinessRuleViolationException) {
            // Business rule violation
            return responseBuilder
                .error(ErrorInfoDto.builder()
                    .code("BUSINESS_RULE_VIOLATION")
                    .detail(throwable.getMessage())
                    .build())
                .build();
        } else {
            // Generic error or unexpected error
            return responseBuilder
                .error(ErrorInfoDto.builder()
                    .code("INTERNAL_ERROR")
                    .detail(status == HttpStatus.INTERNAL_SERVER_ERROR ? 
                        "An unexpected error occurred" : throwable.getMessage())
                    .build())
                .build();
        }
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::errorResponse);
    }
}
