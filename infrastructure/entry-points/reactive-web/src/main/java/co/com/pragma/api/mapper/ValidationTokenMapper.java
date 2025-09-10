package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.response.ValidationTokenResponseDTO;
import co.com.pragma.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class ValidationTokenMapper {
    
    public ValidationTokenResponseDTO toResponse(User user) {
        return new ValidationTokenResponseDTO(
                user.getName(),
                user.getLastName(),
                user.getDateOfBirth(),
                user.getAddress(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getDocumentNumber(),
                user.getRole()
        );
    }
}
