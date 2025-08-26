package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.UserDto;
import co.com.pragma.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public User toEntity(UserDto dto) {
        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .lastName(dto.getLastName())
                .dateOfBirth(dto.getDateOfBirth())
                .address(dto.getAddress())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .documentNumber(dto.getDocumentNumber())
                .roleId(dto.getRoleId())
                .baseSalary(dto.getBaseSalary())
                .build();
    }
    
    public UserDto toDto(User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .lastName(entity.getLastName())
                .dateOfBirth(entity.getDateOfBirth())
                .address(entity.getAddress())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .documentNumber(entity.getDocumentNumber())
                .roleId(entity.getRoleId())
                .baseSalary(entity.getBaseSalary())
                .build();
    }
}
