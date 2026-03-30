package util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilTest {

    @Test
    void testEmail() {
        assertTrue(ValidationUtil.isValidEmail("abc@gmail.com"));
    }
}