package co.com.pragma.api.regex;

public class regex {
  // Validación para nombres y apellidos (solo letras, espacios y tildes)
  public static final String NAME_REGEX = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$";
  public static final String LAST_NAME_REGEX = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$";
  
  // Validación de formato de fecha (YYYY-MM-DD)
  public static final String DATE_REGEX = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$";
  
  // Validación de dirección (letras, números, espacios y caracteres especiales comunes)
  public static final String ADDRESS_REGEX = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s,.#\\-]{2,100}$";
  
  // Validación de teléfono (7-15 dígitos, puede incluir + al inicio para prefijos internacionales)
  public static final String PHONE_NUMBER_REGEX = "^\\+?\\d{7,15}$";
  
  // Validación estándar de correo electrónico
  public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
  
  // Validación para salario base (entre 0 y 15,000,000)
  public static final String SALARY_REGEX = "^(?:[0-9]|[1-9][0-9]{1,6}|1[0-4][0-9]{6}|15000000)(?:\\.\\d{1,2})?$";
}
