package comp3350.group9.theauctionhouse.exception.auth;

import comp3350.group9.theauctionhouse.business.common.Field;

public class UserLoginException extends Exception{
    private final Field[] errorFields;

    public UserLoginException(String msg, Field... errorFields){
        super(msg);
        this.errorFields = errorFields;
    }

    public boolean hasErrorInField(Field f){
        for (Field field : errorFields){
            if(field == f) return true;
        }
        return false;
    }
}
