package co.com.pragma.model.exception;

public class ResourceConflictException extends BusinessRuleViolationException{
    public ResourceConflictException(String message) { super(message);}
}
