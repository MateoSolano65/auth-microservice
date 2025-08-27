package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static co.com.pragma.api.regex.regex.*;

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
    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = NAME_REGEX, message = "El nombre debe contener al menos 2 caracteres y solo puede contener letras y espacios")
    private String name;
    
    @Schema(description = "Apellido del usuario", example = "Pérez")
    @NotBlank(message = "El apellido es obligatorio")
    @Pattern(regexp = LAST_NAME_REGEX, message = "El apellido debe contener al menos 2 caracteres y solo puede contener letras y espacios")
    private String lastName;
    
    @Schema(description = "Fecha de nacimiento", example = "1990-01-01")
    @NotBlank(message = "La fecha de nacimiento es obligatoria")
    @Pattern(regexp = DATE_REGEX, message = "La fecha de nacimiento debe tener el formato AAAA-MM-DD")
    private String dateOfBirth;
    
    @Schema(description = "Dirección de residencia", example = "Calle 123 #45-67")
    @NotBlank(message = "La dirección es obligatoria")
    @Pattern(regexp = ADDRESS_REGEX, message = "La dirección debe contener al menos 2 caracteres y solo puede contener letras, números y caracteres especiales comunes")
    private String address;
    
    @Schema(description = "Número de teléfono", example = "+57 3001234567")
    @NotBlank(message = "El número de teléfono es obligatorio")
    @Pattern(regexp = PHONE_NUMBER_REGEX, message = "El número de teléfono debe tener entre 7 y 15 dígitos")
    private String phoneNumber;
    
    @Schema(description = "Correo electrónico", example = "juan.perez@ejemplo.com")
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Pattern(regexp = EMAIL_REGEX, message = "El correo electrónico debe tener un formato válido")
    private String email;
    
    @Schema(description = "Número de documento de identidad", example = "1234567890")
    @NotBlank(message = "El número de documento es obligatorio")
    private String documentNumber;
    
    @Schema(description = "Identificador del rol", example = "USER")
    @NotBlank(message = "El rol es obligatorio")
    private String role;
    
    @Schema(description = "Salario base", example = "2000000")
    @NotBlank(message = "El salario base es obligatorio")
    @Pattern(regexp = SALARY_REGEX, message = "El salario base debe ser un número entre 0 y 15.000.000")
    private String baseSalary;
}
