package co.com.pragma.model.response;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum ResponseCode {
    
    // Success codes
    USER_CREATED("AU200", "Usuario creado exitosamente"),
    USER_AUTHENTICATED("AU201", "Usuario autenticado exitosamente"),
    TOKEN_GENERATED("AU202", "Token generado exitosamente"),
    USER_FOUND("AU203", "Usuario encontrado exitosamente"),
    
    // Error codes
    VALIDATION_ERROR("VAL-001", "Error de validación"),
    INTERNAL_SERVER_ERROR("API-500", "Error interno del servidor"),
    BAD_REQUEST("API-400", "Solicitud incorrecta"),
    CONFLICT("API-409", "Conflicto con el recurso"),
    UNAUTHORIZED("API-401", "No autorizado"),
    FORBIDDEN("API-403", "Acceso prohibido");
    
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