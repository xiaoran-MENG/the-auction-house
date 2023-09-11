

package comp3350.group9.theauctionhouse.business.common;

public class EmailValidator {
    public enum EmailResult {
        VALID,
        INVALID,
        NULL_VALUE,
        INVALID_USERNAME,
        INVALID_DOMAIN,
        INVALID_LENGTH,
        ERROR_PROCESSING_EMAIL,
    }
    public static EmailResult validate(String email) {
        // Ensures the email is not null
        if (email == null) {
            return EmailResult.NULL_VALUE;
        }

        // Splitting strings is quite unsafe due to rampant nulls
        try {
            String[] emailSplit = email.split("@", 2);

            if (emailSplit[0].length() == 0) {
                return EmailResult.INVALID_USERNAME;
            }

            if (!emailSplit[1].equals("myumanitoba.ca")) {
                return EmailResult.INVALID_DOMAIN;
            }

            if (email.length() > 36){
                return EmailResult.INVALID_LENGTH;
            }
        } catch (Exception e) {
            return EmailResult.ERROR_PROCESSING_EMAIL;
        }

        return EmailResult.VALID;
    }
}
