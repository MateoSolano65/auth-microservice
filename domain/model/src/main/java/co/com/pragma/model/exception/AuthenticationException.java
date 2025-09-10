package co.com.pragma.model.exception;

import co.com.pragma.model.response.ResponseCode;

public class AuthenticationException extends BusinessException {
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    @Override
    public int statusCode() {
        return 401;
    }
    
    @Override
    public String code() {
        return ResponseCode.UNAUTHORIZED.getCodeValue();
    }
}
