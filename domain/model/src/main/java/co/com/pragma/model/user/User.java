package co.com.pragma.model.user;

import lombok.Builder;

import java.math.BigInteger;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
  private Long id;
  private String name;
  private String lastName;
  private String dateOfBirth;
  private String address;
  private String phoneNumber;
  private String email;
  private String documentNumber;
  private String password;
  private String role;
  private String baseSalary;
}
