package util;

import java.util.regex.Pattern;

// validate du lieu dau vao

public class ValidationUtil {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private static final String PHONE_REGEX = "^(0[9])\\\\d{8}$";

    public static boolean isEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isEmpty(email))
            return false;
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone))
            return false;
        return Pattern.matches(PHONE_REGEX, phone);
    }

    public static boolean isPositiveNumber(double value) {
        return value > 0;
    }

    public static boolean isNonNegative(int value) {
        return value >= 0;
    }

    public static boolean isLengthValid(String input, int min, int max) {
        if (isEmpty(input))
            return false;
        int len = input.length();
        return len >= min && len <= max;
    }

    public static boolean isStrongPassword(String password) {
        if (password == null) {
            return false;
        }
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$";
        return password.matches(regex);
    }
}