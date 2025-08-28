package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.UserDto;
import co.com.pragma.model.user.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void toUserDto_whenUserIsNull_shouldReturnNull() {
        UserDto userDto = userMapper.toUserDto(null);
        assertThat(userDto).isNull();
    }

    @Test
    void toDto_whenUserExists_shouldReturnUserUserDto() {

        User user = User.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .phoneNumber("1234567890")
                .role("USER")
                .baseSalary("2000000")
                .build();
                

        UserDto userDto = userMapper.toUserDto(user);
        

        assertThat(userDto.getId()).isEqualTo(user.getId());
        assertThat(userDto.getName()).isEqualTo(user.getName());
        assertThat(userDto.getLastName()).isEqualTo(user.getLastName());
        assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
        assertThat(userDto.getDocumentNumber()).isEqualTo(user.getDocumentNumber());
        assertThat(userDto.getRole()).isEqualTo(user.getRole());
        assertThat(userDto.getBaseSalary()).isEqualTo(user.getBaseSalary());
    }

    @Test
    void toUserDomain_whenUserDtoExists_shouldReturnUser() {

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .documentNumber("123456789")
                .phoneNumber("1234567890")
                .role("USER")
                .baseSalary("2000000")
                .build();
                

        User user = userMapper.toUserDomain(userDto);
        

        assertThat(user.getId()).isEqualTo(userDto.getId());
        assertThat(user.getName()).isEqualTo(userDto.getName());
        assertThat(user.getLastName()).isEqualTo(userDto.getLastName());
        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(user.getDocumentNumber()).isEqualTo(userDto.getDocumentNumber());
        assertThat(user.getRole()).isEqualTo(userDto.getRole());
        assertThat(user.getBaseSalary()).isEqualTo(userDto.getBaseSalary());
    }

    @Test
    void toUserDomain_whenUserDtoIsNull_shouldReturnNull() {
        User user = userMapper.toUserDomain(null);
        assertThat(user).isNull();
    }
}