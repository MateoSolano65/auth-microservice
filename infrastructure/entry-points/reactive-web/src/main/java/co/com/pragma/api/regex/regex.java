package co.com.pragma.api.regex;

public class regex {

  public static final String NAME_REGEX = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$";
  public static final String LAST_NAME_REGEX = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$";
  public static final String DATE_REGEX = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$";
  public static final String ADDRESS_REGEX = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s,.#\\-]{2,100}$";
  public static final String PHONE_NUMBER_REGEX = "^\\+?\\d{7,15}$";
  public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
  public static final String SALARY_REGEX = "^(?:[0-9]|[1-9][0-9]{1,6}|1[0-4][0-9]{6}|15000000)(?:\\.\\d{1,2})?$";
}
