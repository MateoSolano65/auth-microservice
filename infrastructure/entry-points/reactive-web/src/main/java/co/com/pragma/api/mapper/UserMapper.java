package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.UserDto;
import co.com.pragma.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .lastName(dto.getLastName())
                .dateOfBirth(dto.getDateOfBirth())
                .address(dto.getAddress())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .documentNumber(dto.getDocumentNumber())
                .role(dto.getRole())
                .baseSalary(dto.getBaseSalary())
                .build();
    }
    
    public UserDto toDto(User entity) {
        if (entity == null) {
            return null;
        }
        return UserDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .lastName(entity.getLastName())
                .dateOfBirth(entity.getDateOfBirth())
                .address(entity.getAddress())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .documentNumber(entity.getDocumentNumber())
                .role(entity.getRole())
                .baseSalary(entity.getBaseSalary())
                .build();
    }
}
