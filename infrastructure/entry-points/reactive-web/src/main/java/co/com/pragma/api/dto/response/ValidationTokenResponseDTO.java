package co.com.pragma.api.dto.response;

public record ValidationTokenResponseDTO(
        String name,
        String lastName,
        String dateOfBirth,
        String address,
        String phoneNumber,
        String email,
        String documentNumber,
        String role
) {
}
