package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.UserDto;
import co.com.pragma.api.dto.UserResponseDto;
import co.com.pragma.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public User toUserDomain(UserDto dto) {
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
                .password(dto.getPassword())
                .role(dto.getRole())
                .baseSalary(dto.getBaseSalary())
                .build();
    }

    public UserResponseDto toUserResponseDto(User entity) {
        if (entity == null) {
            return null;
        }
        return UserResponseDto.builder()
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
