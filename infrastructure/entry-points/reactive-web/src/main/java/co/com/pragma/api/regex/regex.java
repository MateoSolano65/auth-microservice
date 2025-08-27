package co.com.pragma.api.regex;

public class regex {
  public static final String NAME_REGEX = "^[a-zA-Z]{2,}$";
  public static final String LAST_NAME_REGEX = "^[a-zA-Z]{2,}$";
  public static final String DATE_REGEX = "\\d{4}-\\d{2}-\\d{2}";
  public static final String ADDRESS_REGEX = "^[a-zA-Z0-9\\s]+$";
  public static final String PHONE_NUMBER_REGEX = "^\\d{10}$";
  public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
  public static final String DOCUMENT_NUMBER_REGEX = "^\\d{10}$";
  public static final String SALARY_REGEX = "^(?:\\d{1,7}(?:\\.\\d{1,2})?|15000000(?:\\.00?)?)$";
}
