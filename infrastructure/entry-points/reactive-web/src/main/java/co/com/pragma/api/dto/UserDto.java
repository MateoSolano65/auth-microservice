package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static co.com.pragma.api.regex.regex.*;

import java.math.BigInteger;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(
  description = "Información del usuario",
  requiredProperties = { "name", "lastName", "dateOfBirth", "address", "phoneNumber", "email", "documentNumber", "roleId", "baseSalary" }
)
public class UserDto {
    private Long id;
    
    @Schema(description = "Nombre del usuario", example = "Juan")
    @NotBlank(message = "Name is required")
//    @Pattern(regexp = NAME_REGEX, message = "Name must be at least 2 characters long and contain only letters")
    private String name;
    
    @Schema(description = "Apellido del usuario", example = "Pérez")
    @NotBlank(message = "Last name is required")
//    @Pattern(regexp = LAST_NAME_REGEX, message = "Last name must be at least 2 characters long and contain only letters")
    private String lastName;
    
    @Schema(description = "Fecha de nacimiento", example = "1990-01-01")
    @NotBlank(message = "Date of birth is required")
//    @Pattern(regexp = DATE_REGEX, message = "Date of birth must be in the format YYYY-MM-DD")
    private String dateOfBirth;
    
    @Schema(description = "Dirección de residencia", example = "Calle 123 #45-67")
    @NotBlank(message = "Address is required")
//    @Pattern(regexp = ADDRESS_REGEX, message = "Address must be at least 2 characters long and contain only letters and numbers")
    private String address;
    
    @Schema(description = "Número de teléfono", example = "+57 300 123 4567")
    @NotBlank(message = "Phone number is required")
//    @Pattern(regexp = PHONE_NUMBER_REGEX, message = "Phone number must be 10 digits long")
    private String phoneNumber;
    
    @Schema(description = "Correo electrónico", example = "juan.perez@ejemplo.com")
    @NotBlank(message = "Email is required")
//    @Pattern(regexp = EMAIL_REGEX, message = "Email must be a valid email address")
    private String email;
    
    @Schema(description = "Número de documento de identidad", example = "1234567890")
    @NotBlank(message = "Document number is required")
//    @Pattern(regexp = DOCUMENT_NUMBER_REGEX, message = "Document number must be 10 digits long")
    private String documentNumber;
    
    @Schema(description = "Identificador del rol", example = "USER")
    @NotBlank(message = "Role is required")
    private String role;
    
    @Schema(description = "Salario base", example = "2000000")
    @NotBlank(message = "Base salary is required")
//    @Pattern(regexp = SALARY_REGEX, message = "Base salary must be a valid number")
    private String baseSalary;
}
