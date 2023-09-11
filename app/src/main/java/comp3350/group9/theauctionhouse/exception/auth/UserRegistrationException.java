package comp3350.group9.theauctionhouse.exception.auth;

import java.util.HashMap;
import java.util.Map;

import comp3350.group9.theauctionhouse.business.common.Field;

public class UserRegistrationException extends Exception{
    private final Map<Field,String> messagePerField;

    public UserRegistrationException(String msg, Field field) {
        super("Single field error.");
        this.messagePerField = new HashMap<>();
        messagePerField.put(field,msg);
    }

    public UserRegistrationException(Map<Field,String> map) {
        super("Multi-field Error.");
        this.messagePerField = map;
    }

    public boolean hasErrorInField(Field f){
        return messagePerField.containsKey(f);
    }

    public String getMessageForField(Field f){
        return messagePerField.get(f);
    }
}

