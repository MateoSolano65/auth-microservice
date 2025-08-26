package co.com.pragma.r2dbc.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigInteger;
import java.time.LocalDate;

public class UserData {
  @Id
  @Column("id")
  private Long id;
  @Column("name")
  private String name;
  @Column("last_name")
  private String lastName;
  @Column("date_of_birth")
  private LocalDate dateOfBirth;
  @Column("address")
  private String address;
  @Column("phone_number")
  private String phoneNumber;
  @Column("email")
  private String email;
  @Column("document_number")
  private String documentNumber;
  @Column("role_id")
  private String roleId;
  @Column("base_salary")
  private BigInteger baseSalary;
}

