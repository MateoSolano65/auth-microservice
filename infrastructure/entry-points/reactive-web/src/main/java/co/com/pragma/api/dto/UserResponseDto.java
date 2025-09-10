package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(
  description = "Información del usuario para respuestas",
  requiredProperties = { "name", "lastName", "email", "documentNumber", "role" }
)
public class UserResponseDto {
    private Long id;
    
    @Schema(description = "Nombre del usuario", example = "Juan Carlos")
    private String name;
    
    @Schema(description = "Apellido del usuario", example = "Pérez Gómez")
    private String lastName;
    
    @Schema(description = "Fecha de nacimiento", example = "1990-01-01")
    private String dateOfBirth;
    
    @Schema(description = "Dirección de residencia", example = "Calle 123 #45-67")
    private String address;
    
    @Schema(description = "Número de teléfono", example = "+573001234567")
    private String phoneNumber;
    
    @Schema(description = "Correo electrónico", example = "juan.perez@ejemplo.com")
    private String email;
    
    @Schema(description = "Número de documento de identidad", example = "1234567890")
    private String documentNumber;
    
    @Schema(description = "Identificador del rol", example = "USER")
    private String role;
    
    @Schema(description = "Salario base", example = "2000000")
    private String baseSalary;
}
