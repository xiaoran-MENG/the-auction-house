

package comp3350.group9.theauctionhouse.core.domain;

import java.util.HashMap;
import java.util.Map;

import comp3350.group9.theauctionhouse.business.common.EmailValidator;
import comp3350.group9.theauctionhouse.business.common.Field;
import comp3350.group9.theauctionhouse.exception.auth.UserRegistrationException;

public class User extends Entity {
    public static final int
            MIN_USERNAME_LENGTH = 5, MAX_USERNAME_LENGTH = 30,
            MIN_PASSWORD_LENGTH = 7, MAX_PASSWORD_LENGTH = 30;

    // Performs validation on the fields such as email, etc
    // before returning a user account
    public static User tryCreateAccount(String username, String email, String password) throws UserRegistrationException {
        Map<Field, String> errors = new HashMap<>();

        if (username == null || username.isEmpty()) errors.put(Field.USERNAME, "Empty values present!");
        if (email == null || email.isEmpty())       errors.put(Field.EMAIL, "Empty values present!");
        if (password == null || password.isEmpty()) errors.put(Field.PASSWORD, "Empty values present!");
        if (!errors.isEmpty()) throw new UserRegistrationException(errors);

        // Forces the username to be between 5 - 30 characters long
        if (username.length() < MIN_USERNAME_LENGTH)
            errors.put(Field.USERNAME, "Username needs to be at least " + MIN_USERNAME_LENGTH + " characters long!");
        else if (username.length() > MAX_USERNAME_LENGTH)
            errors.put(Field.USERNAME, "Username needs to be shorter than " + MAX_USERNAME_LENGTH + " characters long!");
        else if (!username.matches("[\\da-zA-Z._#]+"))
            errors.put(Field.USERNAME, "Username cannot contain any special characters (ex. User_Name#)");

        // Forces the password to be between 7 - 30 characters long
        if (password.length() < MIN_PASSWORD_LENGTH)
            errors.put(Field.PASSWORD, "Password needs to be at least " + MIN_PASSWORD_LENGTH + " characters long!");
        else if (password.length() > MAX_PASSWORD_LENGTH)
            errors.put(Field.PASSWORD, "Password needs to be shorter than " + MAX_PASSWORD_LENGTH + " characters long!");

        EmailValidator.EmailResult result = EmailValidator.validate(email);
        if (result != EmailValidator.EmailResult.VALID) {
            switch (result) {
                case INVALID_LENGTH:
                    errors.put(Field.EMAIL, "Email must not be greater than 36 characters.");
                    break;
                case INVALID_DOMAIN:
                    errors.put(Field.EMAIL, "Required University of Manitoba email (ex. XXX@myumanitoba.ca)");
                    break;
                case INVALID_USERNAME:
                    errors.put(Field.EMAIL, "Require more than 0 characters before '@'");
                    break;
                default:
                    errors.put(Field.EMAIL, "Could not process email. Please reformat and try again.");
                    break;
            }
        }
        if (!errors.isEmpty()) throw new UserRegistrationException(errors);
        return new User("temp", username, email, password);
    }

    private final String username;
    private final String email;
    private final String password;

    public User(String id, String username, String email, String password) {
        super(id);
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public final String id() {
        return id;
    }

    public final String username() {
        return username;
    }

    public final String email() {
        return email;
    }

    public final String password() {
        return password;
    }

    // Attempts to login to the account
    public final boolean tryLogin(String password) {
        return this.password.equals(password);
    }
}
