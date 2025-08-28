package co.com.pragma.r2dbc.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;
import java.time.LocalDate;

@Getter
@Setter
@Table("users")
public class UserData {
  @Id
  @Column("id")
  private Long id;
  @Column("name")
  private String name;
  @Column("last_name")
  private String lastName;
  @Column("date_of_birth")
  private String dateOfBirth;
  @Column("address")
  private String address;
  @Column("phone_number")
  private String phoneNumber;
  @Column("email")
  private String email;
  @Column("document_number")
  private String documentNumber;
  @Column("role")
  private String role;
  @Column("base_salary")
  private String baseSalary;
}

