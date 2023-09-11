package comp3350.group9.theauctionhouse.unittests.business.common;


import org.junit.Test;
import static org.junit.Assert.*;
import static comp3350.group9.theauctionhouse.business.common.EmailValidator.EmailResult.INVALID_DOMAIN;
import static comp3350.group9.theauctionhouse.business.common.EmailValidator.EmailResult.INVALID_USERNAME;
import static comp3350.group9.theauctionhouse.business.common.EmailValidator.EmailResult.NULL_VALUE;
import static comp3350.group9.theauctionhouse.business.common.EmailValidator.EmailResult.VALID;
import comp3350.group9.theauctionhouse.business.common.EmailValidator;

public class EmailValidatorTest {

    @Test
    public void testValidEmail() {
        EmailValidator.EmailResult result = EmailValidator.validate("valid@myumanitoba.ca");
        assertEquals(result, VALID);
    }

    @Test
    public void testNullEmail() {
        EmailValidator.EmailResult result = EmailValidator.validate(null);
        assertEquals(result, NULL_VALUE);
    }

    @Test
    public void testEmptyEmail() {
        EmailValidator.EmailResult result = EmailValidator.validate("");
        assertEquals(result, INVALID_USERNAME);
    }

    @Test
    public void testInvalidDomainEmail() {
        EmailValidator.EmailResult result = EmailValidator.validate("invalid@gmail.ca");
        assertEquals(result, INVALID_DOMAIN);
    }

    @Test
    public void testInvalidUsernameEmail() {
        EmailValidator.EmailResult result = EmailValidator.validate("@myumanitoba.ca");
        assertEquals(result, INVALID_USERNAME);
    }
}
