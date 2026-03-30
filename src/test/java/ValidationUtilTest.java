

import org.junit.jupiter.api.Test;

import util.ValidationUtil;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilTest {

    @Test
    void testValidEmail() {
        assertTrue(ValidationUtil.isValidEmail("abc@gmail.com"));
    }

    @Test
    void testInvalidEmail() {
        assertFalse(ValidationUtil.isValidEmail("abcgmail.com"));
    }

    @Test
    void testValidPhone() {
        assertTrue(ValidationUtil.isValidPhone("0123456789"));
    }

    @Test
    void testInvalidPhone() {
        assertFalse(ValidationUtil.isValidPhone("123abc"));
    }

    @Test
    void testValidPrice() {
        assertTrue(ValidationUtil.isPositiveNumber(1000));
    }

    @Test
    void testInvalidPrice() {
        assertFalse(ValidationUtil.isPositiveNumber(-10));
    }

    @Test
    void testStock() {
        assertTrue(ValidationUtil.isNonNegative(0));
        assertFalse(ValidationUtil.isNonNegative(-1));
    }
}