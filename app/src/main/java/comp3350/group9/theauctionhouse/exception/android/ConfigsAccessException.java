package comp3350.group9.theauctionhouse.exception.android;

public class ConfigsAccessException extends Exception {
    public ConfigsAccessException(Exception e){
        super(e);
    }

    public ConfigsAccessException(String e){
        super(e);
    }
}
