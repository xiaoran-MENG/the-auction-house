package comp3350.group9.theauctionhouse.exception.TrustFactor;


public class FailedToRateException extends Exception{
    public FailedToRateException (String cause) {
        super(String.format("Failed to rate: %s", cause));
    }
}
