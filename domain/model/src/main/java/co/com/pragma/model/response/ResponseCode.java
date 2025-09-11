package co.com.pragma.model.response;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum ResponseCode {
    
    // Success codes
    USER_CREATED("AU200", "User created successfully"),
    USER_AUTHENTICATED("AU201", "User authenticated successfully"),
    TOKEN_GENERATED("AU202", "Token generated successfully"),
    USER_FOUND("AU203", "User found successfully"),
    
    // Error codes
    VALIDATION_ERROR("VAL-001", "Validation error"),
    INTERNAL_SERVER_ERROR("API-500", "Internal server error"),
    BAD_REQUEST("API-400", "Bad request"),
    CONFLICT("API-409", "Resource conflict"),
    UNAUTHORIZED("API-401", "Unauthorized"),
    FORBIDDEN("API-403", "Forbidden");
    
    private final String codeValue;
    private final String defaultMessage;
    
    private static final Map<String, ResponseCode> messageMap = new HashMap<>();
    
    static {
        for (ResponseCode code : values()) {
            messageMap.put(code.getDefaultMessage(), code);
        }
    }
    
    ResponseCode(String codeValue, String defaultMessage) {
        this.codeValue = codeValue;
        this.defaultMessage = defaultMessage;
    }
    
    public static ResponseCode findByMessage(String message) {
        return messageMap.get(message);
    }
    
    public static ResponseCode findByHttpStatus(int statusCode) {
        switch (statusCode) {
            case 400:
                return BAD_REQUEST;
            case 401:
                return UNAUTHORIZED;
            case 403:
                return FORBIDDEN;
            case 409:
                return CONFLICT;
            case 422:
                return VALIDATION_ERROR;
            case 500:
            default:
                return INTERNAL_SERVER_ERROR;
        }
    }
}